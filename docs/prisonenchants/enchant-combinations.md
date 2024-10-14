---
description: Allows multiple activated enchants to merge into another
---

# Enchant Combinations

## Config: `combos.yml`

Allows multiple enchants to merge into another

## Configuration

* List two or more enchants to become the result enchant
* The "merge" will only occur when all enchants activate on the same action (block break, etc.)

```yaml
lists:
  '1':
    enchants:
    - CLUSTER_BOMB
    - FORTUNE_BOMB
    result: CLUSTER_FORTUNE_BOMB
  '2':
    enchants:
    - CLUSTER_BOMB
    - VEIN_BOMB
    result: CLUSTER_VEIN_BOMB
```
