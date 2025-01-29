[![Build master](https://img.shields.io/github/actions/workflow/status/AppliedEnergistics/Applied-Energistics-2/build.yml?style=flat-square&branch=master)](https://github.com/AppliedEnergistics/Applied-Energistics-2/actions?query=workflow%3A%22Build+master%22)
[![Latest Release](https://img.shields.io/github/v/release/AppliedEnergistics/Applied-Energistics-2?style=flat-square&label=Release)](https://github.com/AppliedEnergistics/Applied-Energistics-2/releases)
[![Latest PreRelease](https://img.shields.io/github/v/release/AppliedEnergistics/Applied-Energistics-2?include_prereleases&style=flat-square&label=Pre)](https://github.com/AppliedEnergistics/Applied-Energistics-2/releases)

[![Maven Central Version](https://img.shields.io/maven-central/v/org.appliedenergistics/appliedenergistics2)](https://central.sonatype.com/artifact/org.appliedenergistics/appliedenergistics2)

# Applied Energistics 2

## Table of Contents

* [About](#about)
* [Contacts](#contacts)
* [License](#license)
* [Downloads](#downloads)
* [Installation](#installation)
* [Issues](#issues)
* [API](#applied-energistics-2-api)
* [Building](#building)
* [Contribution](#contribution)
* [Localization](#applied-energistics-2-localization)
* [Credits](#credits)

## About

A Minecraft mod about Matter, Energy and using them to conquer the world...

## Contacts

* [Website](https://appliedenergistics.org/)
* [Players Guide](https://guide.appliedenergistics.org/)
* [Discord](https://discord.gg/Zd6t9ka7ne)
* [GitHub](https://github.com/AppliedEnergistics/Applied-Energistics-2)

## License

* Applied Energistics 2 API
  - (c) 2013 - 2020 AlgorithmX2 et al
  - [![License](https://img.shields.io/badge/License-MIT-red.svg?style=flat-square)](http://opensource.org/licenses/MIT)
* Applied Energistics 2
  - (c) 2013 - 2020 AlgorithmX2 et al
  - [![License](https://img.shields.io/badge/License-LGPLv3-blue.svg?style=flat-square)](https://raw.githubusercontent.com/AppliedEnergistics/Applied-Energistics-2/rv2/LICENSE)
* Textures and Models
  - (c) 2020, [Ridanisaurus Rid](https://github.com/Ridanisaurus/), (c) 2013 - 2020 AlgorithmX2 et al
  - [![License](https://img.shields.io/badge/License-CC%20BY--NC--SA%203.0-yellow.svg?style=flat-square)](https://creativecommons.org/licenses/by-nc-sa/3.0/)
* Text and Translations
  - [![License](https://img.shields.io/badge/License-No%20Restriction-green.svg?style=flat-square)](https://creativecommons.org/publicdomain/zero/1.0/)
* Additional Sound Licenses
  - Guidebook Click Sound
    - [EminYILDIRIM](https://freesound.org/people/EminYILDIRIM/sounds/536108/) 
    - [![License](https://img.shields.io/badge/License-CC%20BY%204.0-yellow.svg?style=flat-square)](https://creativecommons.org/licenses/by/4.0/)

## Downloads

Downloads can be found on [CurseForge](https://www.curseforge.com/minecraft/mc-mods/applied-energistics-2), [Modrinth](https://modrinth.com/mod/ae2) or on the [official website](https://appliedenergistics.github.io/download).

## Installation

You install this mod by putting it into the `minecraft/mods/` folder. It has no additional hard dependencies.

## Issues

Applied Energistics 2 crashing, have a suggestion, found a bug?  Create an issue now!

1. Make sure your issue has not already been answered or fixed and you are using the latest version. Also think about whether your issue is a valid one before submitting it.
    * If it is already possible with vanilla and AE2 itself, the suggestion will be considered invalid.
    * Asking for a smaller version, more compact version, or more efficient version of something will also be considered invalid.
2. Go to [the issues page](https://github.com/AppliedEnergistics/Applied-Energistics-2/issues) and click [new issue](https://github.com/AppliedEnergistics/Applied-Energistics-2/issues/new)
3. If applicable, use one of the provided templates. It will also contain further details about required or useful information to add.
4. Click `Submit New Issue`, and wait for feedback!

Providing as many details as possible does help us to find and resolve the issue faster and also you getting a fixed version as fast as possible.

Please note that we might close any issue not matching these requirements. 

## Applied Energistics 2 API

The API for Applied Energistics 2. It is open source to discuss changes, improve documentation, and provide better add-on support in general.

### Maven

AE2 is available on [Maven Central](https://central.sonatype.com/artifact/org.appliedenergistics/appliedenergistics2).

You can use the following snippet as example on how to add a repository to your gradle build file.

    repositories {
        mavenCentral()
    }


We are also available on Github Packages, which you can also use in your builds. Use of Github Packages
[requires special setup](https://docs.github.com/en/packages/using-github-packages-with-your-projects-ecosystem/configuring-gradle-for-use-with-github-packages#authenticating-to-github-packages) to authenticate with your personal access token.

When compiling against the AE2 API you can use gradle dependencies, just add

    dependencies {
        compileOnly "org.appliedenergistics:appliedenergistics2:VERSION:api"
    }

or add the `compileOnly` line to your existing dependencies block to your build.gradle.

Replace `VERSION` with the desired one. With 1.15+ we switched to using [semver](https://semver.org/). 
It is highly recommended following its specification and further considering an upper bound for the dependency version.
A change of the `MAJOR` version will be an API break and can lead to various crashes. Better to inform a player about the addon not supporting the new version until it could be tested or updated.

An example string would be `org.appliedenergistics:appliedenergistics2:12.9.5:api` for the API only or `org.appliedenergistics:appliedenergistics2:12.9.5` for the whole mod.

## Building

1. Clone this repository via 
  - SSH `git clone git@github.com:AppliedEnergistics/Applied-Energistics-2.git` or 
  - HTTPS `git clone https://github.com/AppliedEnergistics/Applied-Energistics-2.git`
2. Build using the `gradlew runData build` command. Jar will be in `build/libs`
3. For core developer: Load the Gradle project in your IDE

## Contribution

Before you want to add major changes, you might want to discuss them with us first, before wasting your time.
If you are still willing to contribute to this project, you can contribute via [Pull-Request](https://help.github.com/articles/creating-a-pull-request).

The [guidelines for contributing](https://github.com/AppliedEnergistics/Applied-Energistics-2/blob/master/.github/CONTRIBUTING.md) contain more detailed information about topics like the used code style and should also be considered.

Here are a few things to keep in mind that will help get your PR approved.

* A PR should be focused on content. Any PRs where the changes are only syntax will be rejected.
* Use the file you are editing as a style guide.
* Consider your feature.
  - Is your suggestion already possible using Vanilla + AE2?
  - Make sure your feature isn't already in the works, or hasn't been rejected previously.
  - Does your feature simplify another feature of AE2? These changes will not be accepted.
  - If your feature can be done by any popular mod, discuss with us first.

**Getting Started**

1. Fork this repository
2. Clone the fork via
  * SSH `git clone git@github.com:<your username>/Applied-Energistics-2.git` or 
  * HTTPS `git clone https://github.com/<your username>/Applied-Energistics-2.git`
3. Change code base
4. Run `gradlew spotlessApply` to apply automatic code formatting
5. Add changes to git `git add -A`
6. Commit changes to your clone `git commit -m "<summary of made changes>"`
7. Push to your fork `git push`
8. Create a Pull-Request on GitHub
9. Wait for review
10. Squash commits for cleaner history

If you are only doing single file pull requests, GitHub supports using a quick way without the need of cloning your fork. Also read up about [synching](https://help.github.com/articles/syncing-a-fork) if you plan to contribute on regular basis.

## Applied Energistics 2 Localization

### English Text

`en_US` is included in this repository, fixes to typos are welcome.

### Encoding

Files must be encoded as UTF-8.

### New or updated Translations

We use Crowdin crowd-sourced translations for our localization. You can participate in localizing Applied Energistics 2 on our [Crowdin Page](https://appliedenergistics2.crowdin.com/applied-energistics-2).

Please keep in mind that we use [String format](https://docs.oracle.com/javase/8/docs/api/java/util/Formatter.html) to pass additional data to the text for displaying.
Therefore you should preserve parts like `%s` or `%1$d%%`, which allows us to replace them with the correct values while you still have the option to change their order for match the rules of grammar.
This might not be possible for some languages. Should this be the case, please contact us.

### Final Note

If you have issues localizing something, feel free to contact us on [Discord](https://discord.gg/b6HZ4p8EKH).

Thanks to everyone helping out to improve localization of AE2.

## Credits

Thanks to all of our [contributors](https://github.com/AppliedEnergistics/Applied-Energistics-2/graphs/contributors)!
