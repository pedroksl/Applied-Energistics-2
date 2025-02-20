package appeng.util;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;

import org.jetbrains.annotations.Nullable;

import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.material.Fluid;

import appeng.api.config.Actionable;
import appeng.api.stacks.AEFluidKey;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.AEKeyType;
import appeng.api.stacks.AEKeyTypes;
import appeng.api.stacks.GenericStack;
import appeng.api.storage.AEKeySlotFilter;
import appeng.helpers.externalstorage.GenericStackInv;
import appeng.me.helpers.BaseActionSource;

/**
 * Configuration inventories contain a set of {@link AEKey} references that configure how certain aspects of a machine
 * work.
 * <p/>
 * They can expose an {@link net.minecraft.world.item.ItemStack} based wrapper that can be used as backing for
 * {@link net.minecraft.world.inventory.Slot} in {@link appeng.menu.AEBaseMenu}.
 * <p/>
 * Primarily their role beyond their base class {@link GenericStackInv} is enforcing the configured filter even on
 * returned keys, not just when setting them.
 */
public class ConfigInventory extends GenericStackInv {
    private final boolean allowOverstacking;

    /**
     * An empty config-type inventory.
     */
    private static final ConfigInventory EMPTY_TYPES = ConfigInventory.configTypes(0).build();

    /**
     * @return A read-only, empty inventory that is in types mode.
     */
    public static ConfigInventory emptyTypes() {
        return EMPTY_TYPES;
    }

    protected ConfigInventory(Set<AEKeyType> supportedTypes, @Nullable AEKeySlotFilter slotFilter,
            Mode mode,
            int size, @Nullable Runnable listener,
            boolean allowOverstacking) {
        super(supportedTypes, listener, mode, size);
        this.allowOverstacking = allowOverstacking;
        setFilter(slotFilter);
    }

    public final static class Builder {
        private final Mode mode;
        private final int size;
        private Set<AEKeyType> supportedTypes = AEKeyTypes.getAll();
        @Nullable
        private AEKeySlotFilter slotFilter;
        @Nullable
        private Runnable changeListener;
        private boolean allowOverstacking;

        private Builder(Mode mode, int size) {
            this.mode = mode;
            this.size = size;
        }

        public Builder supportedType(AEKeyType type) {
            this.supportedTypes = Set.of(type);
            return this;
        }

        public Builder supportedTypes(AEKeyType type, AEKeyType... moreTypes) {
            if (moreTypes.length == 0) {
                return supportedType(type);
            }
            this.supportedTypes = new HashSet<>(1 + moreTypes.length);
            this.supportedTypes.add(type);
            Collections.addAll(this.supportedTypes, moreTypes);
            return this;
        }

        public Builder supportedTypes(Collection<AEKeyType> types) {
            if (types.isEmpty()) {
                throw new IllegalArgumentException("Configuration inventories must support at least one key type");
            }
            this.supportedTypes = Set.copyOf(types);
            return this;
        }

        /**
         * Set a filter that limits what can be inserted to certain slots.
         */
        public Builder slotFilter(AEKeySlotFilter slotFilter) {
            this.slotFilter = slotFilter;
            return this;
        }

        /**
         * Set a filter that applies to all slots at once.
         */
        public Builder slotFilter(Predicate<AEKey> filter) {
            this.slotFilter = (slot, what) -> filter.apply(what);
            return this;
        }

        public Builder changeListener(Runnable changeListener) {
            this.changeListener = changeListener;
            return this;
        }

        /**
         * Allow slots to exceed the natural stack size limits of items. This is false by default.
         */
        public Builder allowOverstacking(boolean enable) {
            this.allowOverstacking = enable;
            return this;
        }

        public ConfigInventory build() {
            return new ConfigInventory(
                    supportedTypes,
                    slotFilter,
                    mode,
                    size,
                    changeListener,
                    allowOverstacking);
        }
    }

    /**
     * @param size The number of slots in this inventory.
     */
    public static Builder configTypes(int size) {
        return new Builder(Mode.CONFIG_TYPES, size);
    }

    /**
     * @param size The number of slots in this inventory.
     */
    public static Builder configStacks(int size) {
        return new Builder(Mode.CONFIG_STACKS, size);
    }

    /**
     * @param size The number of slots in this inventory.
     */
    public static Builder storage(int size) {
        return new Builder(Mode.STORAGE, size);
    }

    @Nullable
    @Override
    public GenericStack getStack(int slot) {
        var stack = super.getStack(slot);
        // Filter and clear stacks not supported by the underlying storage channel
        if (stack != null && !isSupportedType(stack.what())) {
            setStack(slot, null);
            stack = null;
        }
        return stack;
    }

    @Nullable
    @Override
    public AEKey getKey(int slot) {
        var key = super.getKey(slot);
        if (key == null) {
            return null;
        }
        // Filter and clear stacks not supported by the underlying storage channel
        if (!isSupportedType(key)) {
            setStack(slot, null);
            key = null;
        }
        return key;
    }

    public Set<AEKey> keySet() {
        var result = new LinkedHashSet<AEKey>();
        for (int i = 0; i < stacks.length; i++) {
            var what = getKey(i);
            if (what != null) {
                result.add(what);
            }
        }
        return result;
    }

    @Override
    public void setStack(int slot, @Nullable GenericStack stack) {
        if (stack != null) {
            if (!isSupportedType(stack.what())) {
                return;
            }
            boolean typesOnly = mode == Mode.CONFIG_TYPES;
            if (typesOnly && stack.amount() != 0) {
                // force amount to 0 in types-only mode
                stack = new GenericStack(stack.what(), 0);
            } else if (!typesOnly && stack.amount() <= 0) {
                if (mode == Mode.CONFIG_STACKS && getStack(slot) == null) {
                    // filter the slot to a minimum of 1 of that stack, if currently empty
                    // This allows setting a filter using empty containers that are limited to one resource,
                    // e.g. empty mana tablet.
                    stack = new GenericStack(stack.what(), 1);
                } else {
                    // in stack mode, amounts of 0 or less clear the slot
                    stack = null;
                }
            }
        }
        super.setStack(slot, stack);
    }

    @Override
    public long getMaxAmount(AEKey key) {
        if (allowOverstacking)
            return getCapacity(key.getType());
        return super.getMaxAmount(key);
    }

    @Override
    public ConfigMenuInventory createMenuWrapper() {
        return new ConfigMenuInventory(this);
    }

    public ConfigInventory addFilter(ItemLike item) {
        addFilter(AEItemKey.of(item));
        return this;
    }

    public ConfigInventory addFilter(Fluid fluid) {
        addFilter(AEFluidKey.of(fluid));
        return this;
    }

    public ConfigInventory addFilter(AEKey what) {
        Preconditions.checkState(getMode() == Mode.CONFIG_TYPES);
        insert(what, 1, Actionable.MODULATE, new BaseActionSource());
        return this;
    }
}
