---
description: >-
  Edit various boosting factors that can increase or decrease the enchant
  activation chances
---

# Boosts

## Config: `boosts.yml`

## Configuration

* There is both a top level enabled flag and a per-boost enabled flag.
* There is a section which can control the amount of time a player's boosts are cached for.
* There main section is a list of boosts, each with a name and a list of enchants, settings.

```yaml
fishing-frenzy:
  enabled: true
  permissions:
    - prisonenchants.boosts.fishing-frenzy
  enchants:
    1:
      enchant: BAIT
      multiplier: 1.5 # 50% boost
    2:
      enchant: HOOK
      multiplier: 1.75 # 75% boost
# ... and many more
```
