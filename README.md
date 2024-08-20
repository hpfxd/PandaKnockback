# PandaKnockback

PandaKnockback is a plugin for PandaSpigot 1.8.8 providing more customizable player knockback, with features including:

- Profiles with separate settings which can then be applied on a per-player basis
- In-game modification of values
- Ability to create values which are randomly generated within a range every attack
- Capable API for developers to use in plugins

## Download

The plugin is distributed with GitHub releases. To download the most recent version, head to
the [latest release page](https://github.com/hpfxd/PandaKnockback/releases/latest) and download the JAR file.

## Knockback Profiles

Knockback profiles are collections of values for how to apply knockback to players. They can be configured in
the `profiles.yml` file in the plugin directory.

Here is the default profile as an example:

```yaml
default:
  base:
    horizontal: 0.4
    vertical: 0.4

  sprint-bonus:
    horizontal: 0.5
    vertical: 0.1

  knockback-enchantment-bonus:
    horizontal: 0.5
    vertical: 0.05

  pre-multiplier:
    horizontal: 0.5
    vertical: 0.5

  limit:
    vertical: 0.5
```

Profiles can be applied per-player using the `/pandaknockback applyprofile <player> <profile>` command. You can set the
default profile players receive when they join using the `default-profile` configuration setting.

If the server has [WorldGuard](https://worldguard.enginehub.org/) installed, you can also set the `knockback-profile`
flag on a region to apply a specific profile to players within that
region: `/rg flag <region> knockback-profile <profile>`

Learn more about knockback profiles at
the [Knockback Profiles Wiki Page](https://github.com/hpfxd/PandaKnockback/wiki/Knockback-Profiles).

## API

The plugin has an API available for other plugins to use. See
the [API Wiki Page](https://github.com/hpfxd/PandaKnockback/wiki/API) to learn more.
