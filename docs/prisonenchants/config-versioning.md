---
description: Support for automatically updating enchant config files
---

# Config Versioning

## How it Works

* When the plugin is first loaded, the `enchants_config_version` is checked in `config.yml`
* If this version does not match the most recent version, then every individual enchant config file will attempt to update
* If a file's version differs from the current version, then it will update the file
* A file will update through every version
  * For example, v1 to v3 will first go from v1 to v2 then v2 to v3

## Adding a New Version

* Adding a new config version is very easy
* In `ConfigUpdateManager`
  * Add one to the `CONFIG_VERSION` variable
  * Create a new method that will handle the changes. Follow the template of the ones before it
  * Be sure to map the new method in the `mapToUpdateMethod()` method
* In `/example_configs` update the `config_version_history.txt` file to log the changes
* Following these steps will keep the organization consistent for future changes

### A Warning

* Updates individually define **all** ConfigurationSections to copy over. If you add more configuration options you will need to include its section(s) in future updates
* There is currently no backup system. Since all enchant files are edited, **it is your responsibility to create a backup** for when things go wrong

## Notes

* The plugin will only attempt to update the files (on server start) if `enchants_config_version` in `config.yml` does not match the version in the plugin
* If you wish to rerun updates for whatever reason, then you will need to update this value
