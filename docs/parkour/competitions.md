# Competitions

* Competitions give players the chance to compete for the fastest time on a course
* Only one competition can run at a time
* Each player gets one attempt per competition (their attempt will persist if they leave the course or server)
* The top players can be rewarded

## Starting A Competition

* To start a competition, use either of the following commands:
  * `/parkouradmin startCompetition <id> <seconds>` Start a competition
  * `/parkouradmin startRandomCompetition <seconds>` Start a competition on a random course
    * A random course will be chosen that has its start and end locations set
* You can end a competition early with the following command:
  * `/parkouradmin endCompetition <giveRewards>` End the active competition
    * If you pass `false` as the final argument, rewards will **not** be given

## Competition Rewards

* In `rewards.yml` you can define how to reward different placements
* Unless otherwise specified, every competition will reward players

```yaml
rewards:
  '1': # Each entry requires the following 3 fields to be filled
    placements: # The range of finishers to run these commands for (inclusive)
      min: 1
      max: 1
    commands:
    - eco give {player_name} 25000
  '2':
    placements:
      min: 2
      max: 2
    commands:
    - eco give {player_name} 10000
```

* If you want to give all participants a reward, just set min=1 and max=100 (just some large number)
* If the placement range overlaps for two rewards, that placement will receive both rewards
