# Menus

## Course Menu

* The course selection menu allows players to view all courses along with their leaderboards
* This menu dynamically generates per player and responds instantly to course create/delete actions

### Configuration

```yaml
course_select:
  info_item: # Set a help item in the menu. Must be slot 45/46/47/51/52/53
    slot: 53
    material: BOOK
    name: '&eHelp'
    lore:
    - '&7Left click to start course'
    - '&aRight click to load personal leaderboard'
  item:
    name: '{name}' # Parses {id} {name}
    lore: # parses {lb}
    - ''
    - '&7&lCourse Leaderboard:'
    - '{lb}'
    personal_leaderboard_lore: # parses {personal_lb}
    - ''
    - '&7&lPersonal Best Log:'
    - '{personal_lb}'
```
