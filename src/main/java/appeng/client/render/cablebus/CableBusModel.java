/*
 * This file is part of Applied Energistics 2.
 * Copyright (c) 2013 - 2014, AlgorithmX2, All rights reserved.
 *
 * Applied Energistics 2 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Applied Energistics 2 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Applied Energistics 2.  If not, see <http://www.gnu.org/licenses/lgpl>.
 */

package appeng.client.render.cablebus;


import java.util.Collection;
import java.util.Map;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;

import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;

import appeng.core.AELog;
import appeng.core.features.registries.PartModels;


/**
 * The built-in model for the cable bus block.
 */
public class CableBusModel implements IModel
{

	private final PartModels partModels;

	public CableBusModel( PartModels partModels )
	{
		this.partModels = partModels;
	}

	@Override
	public Collection<ResourceLocation> getDependencies()
	{
		partModels.setInitialized( true );
		return partModels.getModels();
	}

	@Override
	public Collection<ResourceLocation> getTextures()
	{
		return CableBuilder.getTextures();
	}

	@Override
	public IBakedModel bake( IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter )
	{

		Map<ResourceLocation, IBakedModel> partModels = loadPartModels( state, format, bakedTextureGetter );

		CableBuilder cableBuilder = new CableBuilder( format, bakedTextureGetter );
		return new CableBusBakedModel( cableBuilder, partModels );
	}

	private Map<ResourceLocation, IBakedModel> loadPartModels( IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter )
	{
		ImmutableMap.Builder<ResourceLocation, IBakedModel> result = ImmutableMap.builder();

		for( ResourceLocation location : partModels.getModels() )
		{
			IModel model = tryLoadPartModel( location );
			IBakedModel bakedModel = model.bake( state, format, bakedTextureGetter );
			result.put( location, bakedModel );
		}

		return result.build();
	}

	private IModel tryLoadPartModel( ResourceLocation location )
	{
		try
		{
			return ModelLoaderRegistry.getModel( location );
		}
		catch( Exception e )
		{
			AELog.error( e, "Unable to load part model " + location );
			return ModelLoaderRegistry.getMissingModel();
		}
	}

	@Override
	public IModelState getDefaultState()
	{
		return TRSRTransformation.identity();
	}
}
