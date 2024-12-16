## Contributing

We would absolutely love to get the community involved, and we welcome any form of contributions – comments and questions on different communication channels, issues and pull request and anything that you build and share using our components.

### Communication channels
* Communication is primarily done using issues.
* If you need support as soon as possible and you can't wait for any pull request, feel free to use [our support](https://xdev.software/en/services/support).
* As a last resort measure or on otherwise important matter you may also [contact us directly](https://xdev.software/en/about-us/contact).

### Ways to help
* **Report bugs**<br/>Create an issue or send a pull request
* **Send pull requests**<br/>If you want to contribute code, check out the development instructions below.
  * However when contributing larger new features, please first discuss the change you wish to make via issue with the owners of this repository before making it.<br/>Otherwise your work might be rejected and your effort was pointless.

We also encourage you to read the [contribution instructions by GitHub](https://docs.github.com/en/get-started/quickstart/contributing-to-projects).

## Developing

### Software Requirements
You should have the following things installed:
* Git
* Java 21 - should be as unmodified as possible (Recommended: [Eclipse Adoptium](https://adoptium.net/temurin/releases/))
* Maven (Note that the [Maven Wrapper](https://maven.apache.org/wrapper/) is shipped with the repo)

### Recommended setup
* Install ``IntelliJ`` (Community Edition is sufficient)
  * Install the following plugins:
    * [Save Actions](https://plugins.jetbrains.com/plugin/22113) - Provides save actions, like running the formatter or adding ``final`` to fields
    * [SonarLint](https://plugins.jetbrains.com/plugin/7973-sonarlint) - CodeStyle/CodeAnalysis
      * You may consider disabling telemetry in the settings under ``Tools > Sonarlint -> About``
    * [Checkstyle-IDEA](https://plugins.jetbrains.com/plugin/1065-checkstyle-idea) - CodeStyle/CodeAnalysis
  * Import the project
  * Ensure that everything is encoded in ``UTF-8``
  * Ensure that the JDK/Java-Version is correct


## Releasing [![Build](https://img.shields.io/github/actions/workflow/status/xdev-software/maven-music-plugin/release.yml?branch=master)](https://github.com/xdev-software/maven-music-plugin/actions/workflows/release.yml)

Before releasing:
* Consider doing a [test-deployment](https://github.com/xdev-software/maven-music-plugin/actions/workflows/test-deploy.yml?query=branch%3Adevelop) before actually releasing.
* Check the [changelog](CHANGELOG.md)

If the ``develop`` is ready for release, create a pull request to the ``master``-Branch and merge the changes

When the release is finished do the following:
* Merge the auto-generated PR (with the incremented version number) back into the ``develop``

### Release failures

There are 2 modes of release failure:
1. The remote server was e.g. down and non of the artifacts got published
2. There was a build failure during release and only parts of the artifacts got released

In case 1 we can re-release the existing version,<br/>in case 2 we have to release a new version when we can't get the artifacts deleted (as is the case with Maven Central)

#### How-to: Re-Releasing an existing version

1. Delete the release on GitHub
2. Delete the release Git tag from the repo (locally and remote!)
3. Delete the ``master``-Branch and re-create it from the ``develop`` branch (or reset it to the state before the release-workflow commits have been done)
    * This requires __temporarily__ removing the branch protection
    * Once this was done a new release is triggered immediately!

#### How-to: Releasing a new version

1. Merge the ``master`` branch back into ``develop`` (or another temporary branch)
2. Make sure all master branch versions are prepared for a new release<br/>e.g. if the broken release was ``1.0.0`` the version should now be at ``1.0.1-SNAPSHOT`` - the ``SNAPSHOT`` is important for the workflow!
3. Mark the broken release as broken e.g. inside the Changelog, GitHub Release page, etc.<br/>
You can use something like this:
    ```
    > [!WARNING]
    > This release is broken as my cat accidentally clicked the abort button during the process
    ```
4. Merge the changes back into the ``master`` branch to trigger a new release
