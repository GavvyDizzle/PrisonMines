---
description: Edit sounds played for enchants and various item actions
---

# Sounds

## Config: `sounds.yml`

## Configuration

* Most sounds are for enchants, but some are for enchanting and/or item actions
* Some sounds are sent only to the player and others are send to a location (for nearby players to hear)
  * Generally sounds that interact with the world or players are sent via location

```yaml
bookSuccessSound:
  sound: BLOCK_NOTE_BLOCK_CHIME
  volume: 1.0
  pitch: 1.4
makeItRainSound:
  sound: ENTITY_DRAGON_FIREBALL_EXPLODE
  volume: 0.5
  pitch: 2.0
# ... and many more
```

* For a list of valid sounds, see [here](https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Sound.html)
* or [here](https://misode.github.io/sounds/) for a sound previewer
