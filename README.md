# Arkanoid Game

A classic Arkanoid/Breakout game implemented in Java with Swing. Break all the bricks using a ball and paddle while collecting power-ups and avoiding losing the ball!

## Features

- **Three Difficulty Levels**: Easy, Medium, and Hard
- **Beautiful Graphics**: Gradient backgrounds, animated elements, and visual effects
- **Power-ups**: 
  - Wide Paddle: Makes your paddle wider for easier ball control
  - Multi-Ball: Adds extra balls to the game
- **Progressive Difficulty**: Ball speed increases over time
- **Animated UI**: Smooth animations and visual feedback
- **Score Tracking**: Game time tracking and ball counter

## How to Run

### Running from IDE
1. Open your Java IDE (IntelliJ IDEA, Eclipse, VS Code, etc.)
2. Import or open the project folder
3. Run the `Main.java` file

## How to Play

### Game Start
1. Launch the game to see the main menu
2. Choose your difficulty level:
   - **Easy**: Slower ball, wider paddle, more power-ups
   - **Medium**: Moderate speed, some indestructible bricks
   - **Hard**: Fast ball, narrow paddle, more indestructible bricks

### Controls
- **Left Arrow Key**: Move paddle left OR launch ball to the left
- **Right Arrow Key**: Move paddle right OR launch ball to the right  
- **Up Arrow Key**: Launch ball straight up (random left/right direction)

### Gameplay
1. **Launch the Ball**: When the game starts, press any arrow key to launch the ball
2. **Control the Paddle**: Use left and right arrow keys to move your paddle
3. **Break Bricks**: Hit colored bricks with the ball to destroy them
4. **Avoid Gray Bricks**: Gray bricks are indestructible and will bounce the ball back
5. **Collect Power-ups**: Catch falling power-ups with your paddle:
   - **Green (——)**: Wide Paddle - Makes your paddle wider for 10 seconds
   - **Gold (×2)**: Multi-Ball - Adds two extra balls to the game
6. **Don't Let the Ball Fall**: If all balls fall below the paddle, you lose!

### Winning and Losing
- **Win**: Destroy all destructible (colored) bricks
- **Lose**: Let all balls fall below the paddle
- **After winning**: Choose to advance to the next difficulty, replay current level, or exit
- **After losing**: Choose to restart or exit

### Game Elements

#### Bricks
- **Colored Bricks**: Can be destroyed, arranged in rainbow pattern
- **Gray Bricks**: Indestructible, more common on higher difficulties
- **Power-up Chance**: Some bricks drop power-ups when destroyed

#### Ball Physics
- Ball speed increases over time
- Ball direction changes based on where it hits the paddle:
  - Center: Straight up
  - Edges: Angled bounce
- Ball bounces off walls, ceiling, paddle, and bricks

#### Power-ups
- **Wide Paddle**: Increases paddle width by 80% for 10 seconds
- **Multi-Ball**: Creates two additional balls from the current ball position

## Difficulty Differences

| Feature | Easy | Medium | Hard |
|---------|------|--------|------|
| Ball Speed | 3 units | 4 units | 5 units |
| Paddle Width | 150px | 120px | 100px |
| Paddle Speed | 8-15 | 10-18 | 12-20 |
| Speed Increase | Every 10s | Every 6s | Every 3s |
| Indestructible Bricks | None | 15% chance | 25% chance + top row |
| Power-up Drop Rate | 30% | 20% | 15% |

## File Structure

- `Main.java` - Main game class and menu system
- `Ball.java` - Ball object class (defined within Main.java)
- `Brick.java` - Brick object properties and behavior
- `PowerUp.java` - Power-up system implementation

