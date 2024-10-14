# Courses

* Course configurations exist in the `/Parkour/courses` folder
* Each course defines a different parkour course that players can join
* Each course automatically has a leaderboard created for it

## Timing

* This plugin uses a tick based timing system. This means that times will be in 0.05 second increments
* Starts and finishes of all players are batched and run at the same time. This ensures that all times are consistent and fair
* A pressure plate is used to detect finishes so the server knows exactly when a finish occurs

### Lag Detection

* Unfortunately, this system is susceptible to server lag
* To combat this, there is an optional setting in `config.yml` to discard "laggy" finishes

```yaml
lag_detection:
  enabled: true
  allowable_millis_range: 50
```

* If enabled, a player's real-time completion time is compared against their "tick" time
* When these times are too far apart, their time will be discarded (competitions allow a rerun)
  * ex. I finish a course in 20 ticks but my real time says it took 1200ms. Since this difference of 200 is greater than 50, my time will be discarded
* **IMPORTANT** - Due to how the real-time is calculated, the range will fall anywhere between 0 and \~45 milliseconds. Setting the allowable range to 50 will ensure at most a 1 tick discrepancy in a player's time

## Creating A Course

* There are two ways to create a new course
  * `/parkouradmin create <id>` Create a new course (preferred)
  * `/parkouradmin loadFromFile <id>` Load a new course from an existing file
* Next, set the start and finish locations with the `/parkourAdmin setLocation ...` command
  * If the course is read in from a file, all of its settings will be read in too
* After these 2 steps, a course will be ready for players to use. There are a few optional settings for you to change
  * Teleport Location - You can define a pressure plate that will trigger a course restart with `/parkouradmin setLocation <id> teleport`
  * Name - Set the name of a course appear in leaderboard and placeholder output with `/parkouradmin set <id> name ...`
  * Material - The material of the item for this course in the course menu with `/parkouradmin set <id> material <material>`
  * Death Plane Override - Set the height of the death plane for this course only with `/parkouradmin set <id> death_boundary_height <height>`

## Course Deletion

* After deleting a course via the `/parkouradmin delete <id>` command, a few things will happen:
  * The course will be completely removed from the server and the menu will remove it immediately
  * The database will delete all times, competitions, and competition times associated with a course
  * The course's file will be deleted
