package search_algorithm;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Collections;
import java.util.Queue;
import java.util.LinkedList;
import lejos.hardware.ev3.EV3;
import lejos.hardware.lcd.TextLCD;
import lejos.hardware.motor.Motor;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3IRSensor;
import lejos.hardware.BrickFinder;
import lejos.hardware.Keys;
import lejos.robotics.Color;
import lejos.robotics.RegulatedMotor;
import lejos.robotics.SampleProvider;
import lejos.utility.Delay;

public class search_algorithm {
    // global var related to robot.
    public static boolean testing = true; // a variable for testing robot-related function
    public static boolean noNeed = true;
    
    public static RegulatedMotor leftMotor = Motor. A;
    public static RegulatedMotor rightMotor = Motor. B;
    public static EV3IRSensor sensor = new EV3IRSensor(SensorPort. S1);
    public static EV3ColorSensor color_sensor = new EV3ColorSensor(SensorPort. S2);
    
    public static EV3 ev3 = (EV3) BrickFinder.getLocal();
    public static TextLCD lcd = ev3.getTextLCD();
    public static Keys keys = ev3.getKeys();

    // robot related functions.
    // move_forward() : move forward
    // left_turn() : turn left
    // right_turn() : turn right
    // is_box() : return true if box is in front of robot, else false
    // is_red() : return true if floor is red, else false
    // select_start_point() : true if start point is {0,0}, else false
    // turn_robot(curr_direction, target_direction) : turn robot at curr_direction to target_direction
    // find_path(map, current_pos, target_pos) : calculate way to go target_pos with a map
    // find_nearest_pos(current_pos, visited) : find position that we un-visited
    // is_right_position(pos) : true if pos is in a map, else false

    public static void move_forward() {
        if(testing) {
            lcd.clear();
            lcd.drawString("move_forward", 1, 4);
        }
        
        leftMotor.synchronizeWith(new RegulatedMotor[] {rightMotor});
        leftMotor.startSynchronization();
        leftMotor.setSpeed(400);
        rightMotor.setSpeed(400);
        leftMotor.setAcceleration(400);
        rightMotor.setAcceleration(400);
        leftMotor.rotate(657);
        rightMotor.rotate(657);
        leftMotor.endSynchronization();

        Delay.msDelay(3000);
        return;
    }

    public static void left_turn() {
        if(testing) {
            lcd.clear();
            lcd.drawString("left_turn", 1, 4);
        }
        
        leftMotor.setSpeed(400);
        rightMotor.setSpeed(400);
        leftMotor.setAcceleration(400);
        rightMotor.setAcceleration(400);
        leftMotor.rotate(-350);
        rightMotor.rotate(350);
        leftMotor.rotate(180);
        return;
    }
    public static void right_turn() {
        if(testing) {
            lcd.clear();
            lcd.drawString("right_turn", 1, 4);
        }
        
        leftMotor.setSpeed(400);
        rightMotor.setSpeed(400);
        leftMotor.setAcceleration(400);
        rightMotor.setAcceleration(400);
        rightMotor.rotate(-350);
        leftMotor.rotate(350);
        rightMotor.rotate(175);
        return;
    }

    public static boolean is_box() {
        Delay.msDelay(1000); // process loads after a second delay
        
        SampleProvider distanceMode = sensor.getDistanceMode();
        float value[] = new float[distanceMode.sampleSize()];
        
        distanceMode.fetchSample(value, 0);
        float centimeter = value[0];
        
        if(testing) {
            lcd.clear();
            lcd.drawString("Distance : " + centimeter, 1, 4);
            
            Delay.msDelay(1000); // a second to show the centimeter
        }
        
        if(centimeter < 23) {
            return true;
        } else return false;
    }

    public static boolean is_red() {
        int color_id = color_sensor.getColorID();
        
        if(testing || noNeed) {
            lcd.clear();
            lcd.drawString("Red? " + (color_id == Color.RED), 1, 4);
            
            Delay.msDelay(1000); // a second to show the centimeter
        }
        
        if(color_id == Color.RED) {
            return true;
        } else return false;
    }

    public static boolean select_start_point() {
        lcd.clear();
        lcd.drawString ("{0,0} : <, {5,3} : >", 1, 4);

        if (keys.waitForAnyPress() == Keys.ID_LEFT) {
            return true;
        } else {
            return false;
        }
    }

    public static void turn_robot(int curr_direction, int target_direction) {
        int diff = target_direction - curr_direction;
        if (diff == -3) {
            right_turn();
        }
        else if (diff == 3) {
            left_turn();
        }
        else if (diff < 0) {
            for (int i = 0; i < Math.abs(diff); i++) {
                left_turn();
            }
        }
        else {
            for (int i = 0; i < Math.abs(diff); i++) {
                right_turn();
            }
        }
    }
    
    public static ArrayList<Integer> find_path(int[][] map, int[] current_pos, int[] target_pos) {
        ArrayList<Integer> movement = new ArrayList<Integer>();
        ArrayList<int[]> temp = new ArrayList<int[]>();
        Queue<int[]> queue = new LinkedList<>();

        int[][] drdc = { { 0, 1 }, { 1, 0 }, { 0, -1 }, { -1, 0 } };
        int parent_idx = -1;
        int curr_idx = 0;
        boolean find = false;

        // BFS algorithm
        // x, y, curr_idx(in temp), parent_idx(in temp);
        queue.add(new int[]{current_pos[0], current_pos[1], curr_idx, parent_idx});

        // for traversing path
        temp.add(new int[]{current_pos[0], current_pos[1], curr_idx, parent_idx});
        curr_idx++;

        while(queue.size() != 0) {
            int[] top = queue.poll();
            int parent = top[2]; // idx;
            for (int i = 0; i < 4; i++) {
                int[] d = drdc[i];
                int[] new_pos = {top[0] + d[0], top[1] + d[1]};

                // if top of queue is target position, stop BFS
                if (new_pos[0] == target_pos[0] && new_pos[1] == target_pos[1]) {
                    temp.add(new int[]{new_pos[0], new_pos[1], curr_idx, parent});
                    find = true;
                    break;
                }

                // push coordinate which is inside the grid and not a box
                if (is_right_position(new_pos) && map[new_pos[0]][new_pos[1]] != 2) {
                    queue.add(new int[]{new_pos[0], new_pos[1], curr_idx, parent});
                    temp.add(new int[]{new_pos[0], new_pos[1], curr_idx, parent});
                    curr_idx++;
                }
            }
            if (find) {
                break;
            }
        }

        if (!find) {
            return movement; // cannot found path, return empty path;
        }

        int[] a = temp.get(temp.size() - 1); // get last element, which is target position;
        while (a[3] != -1) {
            int[] b = temp.get(a[3]); // get parent;
            int dx = a[0] - b[0];
            int dy = a[1] - b[1];

            // calculate direction;
            if (dx == 0) {
                if (dy == 1) {
                    movement.add(0);
                }
                else {
                    movement.add(2);
                }
            }
            else {
                if (dx == 1) {
                    movement.add(1);
                }
                else {
                    movement.add(3);
                }
            }
            a = b;
        } 

        Collections.reverse(movement);
        return movement;
    }
    
    public static int[] find_nearest_pos(int[] current_pos, boolean[][] visited, int direction) {
        int radius = 0;
        int curr_x = current_pos[0];
        int curr_y = current_pos[1];
        
        int[] nearest_pos;
        int[][] drdc = {{0,1}, {1,0}, {0,-1}, {-1,0}};
        int[] d = drdc[direction];
        int[] next_pos = new int[] {current_pos[0] + d[0], current_pos[1] + d[1]};
        
        // if grid in front of robot is un-visited grid, just go there.
        if (is_right_position(next_pos) && !visited[next_pos[0]][next_pos[1]]) {
            return next_pos;
        }
        
        // else
        while (true) {
            radius++;
            // from front-right to back-left..
            for (int j = curr_y + radius; j >= curr_y - radius; j--) {
                for (int i = curr_x + radius; i >= curr_x - radius; i--) {
                    // if (i == curr_x - radius || i == curr_x + radius || j == curr_y - radius || j == curr_y + radius) {
                    if ((Math.abs(i - curr_x) + Math.abs(j - curr_y)) == radius) {
                        if (is_right_position(new int[]{i, j}) && !visited[i][j]) {
                            nearest_pos = new int[]{i, j};
                            return nearest_pos;
                        }
                    }
                }
            }
            if (radius >= 10) { // all cells are visited
                nearest_pos = new int[]{-1, -1};
                break;
            }
        }
        return nearest_pos;
    }
    
    public static boolean is_right_position(int[] position) {
        if (position[0] < 0 || position[0] >= 6 || position[1] < 0 || position[1] >= 4) {
            return false;
        } else {
            return true;
        }
    }
    
    public static void main(String[] args) {
        int[][] drdc = { { 0, 1 }, { 1, 0 }, { 0, -1 }, { -1, 0 } };
        boolean[][] visited = new boolean[6][4];
        int[] original_pos;
        int[][] map = new int[6][4]; // map that robot knows.
        int direction = 0; // 0 = up, 1 = right, 2 = down, 3 = left
        
        if (select_start_point()) {
            original_pos = new int[] { 0, 0 };
            direction = 0;
        } else {
            original_pos = new int[] { 5, 3 };
            direction = 2;
        }

        int[] current_pos = original_pos;
        
        // answer
        ArrayList<int[]> red_found = new ArrayList<int[]>();
        ArrayList<int[]> box_found = new ArrayList<int[]>();
        
        boolean finished = false;
        int found_num = 0;
        
        // moving until visit all cells
        do {
            int[] target_pos = find_nearest_pos(current_pos, visited, direction);
            
            while (current_pos[0] != target_pos[0] || current_pos[1] != target_pos[1]) {
                // calculate path from current_position to target_position
                ArrayList<Integer> path;

                if (current_pos[0] == original_pos[0] && current_pos[1] == original_pos[1] && finished) {
                    break;
                }
                
                // if find all targets or visit all cells, go to the start point
                if (found_num == 4 || (target_pos[0] == -1 && target_pos[1] == -1)) {
                    finished = true;
                    path = find_path(map, current_pos, original_pos);

                // if target position is box(found while moving)
                } else if (visited[target_pos[0]][target_pos[1]]) {
                    break;
                } else {
                    path = find_path(map, current_pos, target_pos);
                }
                
                // move along with calculated path
                for (int way: path) {
                    if(testing) {
                        lcd.clear();
                        lcd.drawString("cur_pos: " + current_pos[0] + ", " + current_pos[1], 1, 4);
                        Delay.msDelay(1000);
                    }

                    // first, check this is visited grid;
                    if (!finished && !visited[current_pos[0]][current_pos[1]]) {
                        if (is_red()) { // check_red_block
                            red_found.add(current_pos);
                            found_num++;
                        }
                        visited[current_pos[0]][current_pos[1]] = true;
                    }
                    
                    // if robot's heading is different from way to go
                    if (way != direction) {
                        turn_robot(direction, way);
                        direction = way;
                    }

                    int[] d = drdc[way];
                    int[] new_pos = {current_pos[0] + d[0], current_pos[1] + d[1]};
                    
                    // if box is found while moving, add box to map and recalculate path
                    // with updated map
                    if (!finished && is_box()) {
                        if (!visited[new_pos[0]][new_pos[1]]) {
                            box_found.add(new_pos);
                            found_num++;
                            map[new_pos[0]][new_pos[1]] = 2;
                            visited[new_pos[0]][new_pos[1]] = true;
                        }
                        break; // recalculate path

                    } else { // move
                        move_forward();
                        current_pos = new_pos;
                    }
                }
            }
            
            if (finished) { // end moving
                break;
            }
        } while(keys.getButtons() != Keys.ID_ESCAPE);

        lcd.clear();
        int line_count = 0;

        // print out targets
        for (int[] item: red_found) {
            lcd.drawString("(" + item[0] + "," + item[1] + "," + "R)", 0, line_count++);
        }
        for (int[] item: box_found) {
            lcd.drawString("(" + item[0] + "," + item[1] + "," + "B)", 0, line_count++);
        }

        // wait...
        do {
            Delay.msDelay(10000);
        } while (keys.getButtons() != Keys.ID_ESCAPE);
    }
}

