package com.example.snake;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Main extends Application {
    // Variables
    static int speed = 6;
    static int foodcolor = 0;
    static int width = 15;
    static int height = 15;
    static int cornersize = 40;
    static List<Corner> snake = new ArrayList<>();
    static Dir direction = Dir.left;
    static boolean gameOver = false;
    static Random rand = new Random();
    static int foodX;
    static int foodY;

    public enum Dir {
        left, right, up, down
    }

    public static class Corner {
        int x;
        int y;

        public Corner(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    public void start(Stage primaryStage) {
        try {
            newFood();

            BorderPane root = new BorderPane();
            Canvas c = new Canvas(width * cornersize, height * cornersize);
            GraphicsContext gc = c.getGraphicsContext2D();
            root.setCenter(c);

            // Create a VBox for Score
            VBox upperVBox = new VBox();
            upperVBox.setAlignment(Pos.CENTER);
            upperVBox.setStyle("-fx-background-color: #333333;");
            Text scoreText = new Text("Score: " + (speed - 6));
            scoreText.setFill(Color.WHITE);
            scoreText.setFont(Font.font("", 30));
            upperVBox.getChildren().add(scoreText);
            root.setTop(upperVBox);

            StackPane scorePane = new StackPane();
            scorePane.setStyle("-fx-background-color: #333333;");
            Button restartButton = new Button("Restart Game");
            restartButton.setStyle("-fx-font-size: 20px;");
            scorePane.getChildren().add(restartButton);
            root.setBottom(scorePane);

            new AnimationTimer() {
                long lastTick = 0;

                public void handle(long now) {
                    if (lastTick == 0) {
                        lastTick = now;
                        tick(gc, scoreText);
                        return;
                    }

                    if (now - lastTick > 1000000000 / speed) {
                        lastTick = now;
                        tick(gc, scoreText);
                    }
                }
            }.start();

            Scene scene = new Scene(root, Color.BLACK);

            scene.addEventFilter(KeyEvent.KEY_PRESSED, key -> {
                KeyCode code = key.getCode();
                if ((code == KeyCode.W || code == KeyCode.UP) && direction != Dir.down) {
                    direction = Dir.up;
                } else if ((code == KeyCode.A || code == KeyCode.LEFT) && direction != Dir.right) {
                    direction = Dir.left;
                } else if ((code == KeyCode.S || code == KeyCode.DOWN) && direction != Dir.up) {
                    direction = Dir.down;
                } else if ((code == KeyCode.D || code == KeyCode.RIGHT) && direction != Dir.left) {
                    direction = Dir.right;
                }
            });

            snake.add(new Corner(width / 2, height / 2));
            snake.add(new Corner(width / 2, height / 2));
            snake.add(new Corner(width / 2, height / 2));

            primaryStage.setScene(scene);
            primaryStage.setTitle("SNAKE GAME");
            primaryStage.setResizable(true);
            primaryStage.show();

            restartButton.setOnAction(event -> {
                snake.clear();
                snake.add(new Corner(width / 2, height / 2));
                snake.add(new Corner(width / 2, height / 2));
                snake.add(new Corner(width / 2, height / 2));
                direction = Dir.left;
                gameOver = false;
                speed = 5;
                newFood();
                scoreText.setText("Score: " + (speed - 6));
            });

            c.requestFocus();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void tick(GraphicsContext gc, Text scoreText) {
        if (gameOver) {
            gc.setFill(Color.RED);
            gc.setFont(new Font("", 50));
            gc.fillText("GAME OVER", (width * cornersize - 300) / 2, (height * cornersize) / 2);
            return;
        }

        for (int i = snake.size() - 1; i >= 1; i--) {
            snake.get(i).x = snake.get(i - 1).x;
            snake.get(i).y = snake.get(i - 1).y;
        }

        switch (direction) {
            case up:
                snake.get(0).y--;
                if (snake.get(0).y < 0) {
                    snake.get(0).y = height - 1;
                }
                break;
            case down:
                snake.get(0).y++;
                if (snake.get(0).y >= height) {
                    snake.get(0).y = 0;
                }
                break;
            case left:
                snake.get(0).x--;
                if (snake.get(0).x < 0) {
                    snake.get(0).x = width - 1;
                }
                break;
            case right:
                snake.get(0).x++;
                if (snake.get(0).x >= width) {
                    snake.get(0).x = 0;
                }
                break;
        }

        if (foodX == snake.get(0).x && foodY == snake.get(0).y) {
            snake.add(new Corner(-1, -1));
            newFood();
            speed++;
            scoreText.setText("Score: " + (speed - 6));
        }

        for (int i = 1; i < snake.size(); i++) {
            if (snake.get(0).x == snake.get(i).x && snake.get(0).y == snake.get(i).y) {
                gameOver = true;
            }
        }

        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, width * cornersize, height * cornersize);

        Color cc = Color.WHITE;

        switch (foodcolor) {
            case 0:
                cc = Color.PURPLE;
                break;
            case 1:
                cc = Color.BLUE;
                break;
            case 2:
                cc = Color.RED;
                break;
            case 3:
                cc = Color.HOTPINK;
                break;
            case 4:
                cc = Color.YELLOW;
                break;
        }
        gc.setFill(cc);
        gc.fillOval(foodX * cornersize, foodY * cornersize, cornersize, cornersize);

        for (int i = 0; i < snake.size(); i++) {
            int gap = 5;

            if (i == 0) {
                gc.setFill(Color.BLUE);
                gc.fillRect(snake.get(i).x * cornersize, snake.get(i).y * cornersize, cornersize - gap, cornersize - gap);
                gc.setFill(Color.HOTPINK);
                gc.fillRect(snake.get(i).x * cornersize, snake.get(i).y * cornersize, cornersize - gap * 2, cornersize - gap * 2);
            } else {
                gc.setFill(Color.BLUE);
                gc.fillRect(snake.get(i).x * cornersize, snake.get(i).y * cornersize, cornersize - gap, cornersize - gap);
                gc.setFill(Color.DARKBLUE);
                gc.fillRect(snake.get(i).x * cornersize, snake.get(i).y * cornersize, cornersize - gap * 2, cornersize - gap * 2);
            }
        }
    }

    public static void newFood() {
        start: while (true) {
            foodX = rand.nextInt(width);
            foodY = rand.nextInt(height);

            for (Corner c : snake) {
                if (c.x == foodX && c.y == foodY) {
                    continue start;
                }
            }
            foodcolor = rand.nextInt(5);
            break;
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
