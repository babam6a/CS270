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
	public static boolean testing = false; // a variable for testing robot-related function
	public static boolean noNeed = true;
	
	public static RegulatedMotor leftMotor = Motor. A;
	public static RegulatedMotor rightMotor = Motor. B;
	public static EV3IRSensor sensor = new EV3IRSensor(SensorPort. S1);
	public static EV3ColorSensor color_sensor = new EV3ColorSensor(SensorPort. S2);
	
	public static EV3 ev3 = (EV3) BrickFinder.getLocal();
	public static TextLCD lcd = ev3.getTextLCD();
	public static Keys keys = ev3.getKeys();
	// #########################################################
	// robot related functions are here
	// move_forward() : move forward
	// left_turn() : turn left
	// right_turn() : turn right
	// is_box() : return true if box is in front of robot, else false
	// is_red() : return true if floor is red, else false
	// select_start_point() : true if start point is {0,0}, else false

	public static void move_forward() {
		if(testing) {
			lcd.clear();
			lcd.drawString("move_forward", 1, 4);
		}
		
		leftMotor.synchronizeWith(new RegulatedMotor[] {rightMotor});
		leftMotor.startSynchronization();
		leftMotor.setSpeed(400);
		rightMotor.setSpeed(400);
		leftMotor.setAcceleration(800);
		rightMotor.setAcceleration(800);
		leftMotor.rotate(690);
		rightMotor.rotate(690);
		leftMotor.endSynchronization();
		
		Delay.msDelay(2700);

		return;
	}
	public static void left_turn() {
		if(testing) {
			lcd.clear();
			lcd.drawString("left_turn", 1, 4);
		}

		leftMotor.synchronizeWith(new RegulatedMotor[] {rightMotor});
		leftMotor.startSynchronization();
		
		leftMotor.setSpeed(400);
		rightMotor.setSpeed(400);
		leftMotor.setAcceleration(800);
		rightMotor.setAcceleration(800);
		
		leftMotor.rotate(-220);
		rightMotor.rotate(220);
		
		leftMotor.endSynchronization();
		Delay.msDelay(1900);
        return;
    }
    public static void right_turn() {
		if(testing) {
			lcd.clear();
			lcd.drawString("right_turn", 1, 4);
		}		
		leftMotor.synchronizeWith(new RegulatedMotor[] {rightMotor});
		leftMotor.startSynchronization();
		
		leftMotor.setSpeed(400);
		rightMotor.setSpeed(400);
		leftMotor.setAcceleration(800);
		rightMotor.setAcceleration(800);
		
		leftMotor.rotate(232);
		rightMotor.rotate(-232);
		
		leftMotor.endSynchronization();
		Delay.msDelay(1900);
        return;
    }

	public static boolean is_box() {		
		SampleProvider distanceMode = sensor.getDistanceMode();
		float value[] = new float[distanceMode.sampleSize()];
		
		distanceMode.fetchSample(value, 0);
		float centimeter = value[0];
		
		if(testing) {
			lcd.clear();
			lcd.drawString("Distance : " + centimeter, 1, 4);
			
			Delay.msDelay(1000); // a second to show the centimeter
		}
		
		if(centimeter < 30) {
			return true;
		} else return false; // must be changed to account for sensor problem
		
		// if (real_map[position[0]][position[1]] == 2) {
		// 	return true;
		// } else {
		// 	return false;
		// }
	}

	public static boolean is_red() {
		int color_id = color_sensor.getColorID();
		
		if(testing && noNeed) {
			lcd.clear();
			lcd.drawString("Red? " + (color_id == Color.RED), 1, 4);
			
			Delay.msDelay(1000); // a second to show the centimeter
		}
		
		if(color_id == Color.RED) {
			return true;
		} else return false;
		
		// if (real_map[position[0]][position[1]] == 1) {
		// 	return true;
		// } else {
		// 	return false;
		// }
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
	// to here.
	// #########################################################
	
	// turn_robot(curr_direction, target_direction) : turn robot at curr_direction to target_direction
	// find_path(map, current_pos, target_pos) : calculate way to go target_pos with a map
	// find_nearest_pos(current_pos, visited) : find position that we un-visited
	// is_right_position(pos) : true if pos is in a map, else false
	// main() : move robot with calculated path and if box and red detected, append it to array

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
        // int dx = target_pos[0] - current_pos[0];
        // int dy = target_pos[1] - current_pos[1];
        ArrayList<Integer> movement = new ArrayList<Integer>();
		ArrayList<int[]> temp = new ArrayList<int[]>();
		Queue<int[]> queue = new LinkedList<>();

		int[][] drdc = { { 0, 1 }, { 1, 0 }, { 0, -1 }, { -1, 0 } };
		int parent_idx = -1;
		int curr_idx = 0;
		boolean find = false;

		// with BFS algorithm
		// x, y, my_idx(in temp), parent_idx(in temp);
		queue.add(new int[]{current_pos[0], current_pos[1], curr_idx, parent_idx});
		temp.add(new int[]{current_pos[0], current_pos[1], curr_idx, parent_idx});
		curr_idx++;

		while(queue.size() != 0) {
			int[] top = queue.poll();
			int parent = top[2]; // idx;
			for (int i = 0; i < 4; i++) {
				int[] d = drdc[i];
				int[] new_pos = {top[0] + d[0], top[1] + d[1]};

				if (new_pos[0] == target_pos[0] && new_pos[1] == target_pos[1]) {
					temp.add(new int[]{new_pos[0], new_pos[1], curr_idx, parent});
					find = true;
					break;
				}

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
			return movement; // cannot found path;
		}

		int[] a = temp.get(temp.size() - 1); // get last element, which is target position;
		while (a[3] != -1) {
			int[] b = temp.get(a[3]); // get parent;
			int dx = a[0] - b[0];
			int dy = a[1] - b[1];

			// determine direction;
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
        
        // for (int i = 0; i < Math.abs(dy); i++) {
        //     if (dy < 0) {
        //         movement.add(2); // down
        //     } else {
        //         movement.add(0); // up
        //     }
        // }
        // for (int i = 0; i < Math.abs(dx); i++) {
        //     if (dx < 0) {
        //         movement.add(3); // left
        //     } else {
        //         movement.add(1); // right
        //     }
        // }
        
        // Collections.shuffle(movement); // shuffle movement to avoid infinite loop

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
        
        if (is_right_position(next_pos) && !visited[next_pos[0]][next_pos[1]]) {
        	return next_pos;
        }
        
        while (true) {
            radius++;
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
		int[][] map = new int[6][4];
		int direction = 0; // 0 = up, 1 = right, 2 = down, 3 = left
		
		if (select_start_point()) {
			original_pos = new int[] { 0, 0 };
			direction = 0;
		} else {
			original_pos = new int[] { 5, 3 };
			direction = 2;
		}
		int[] current_pos = original_pos;
		
	// s #########################################################
		// for debugging. not used in real test
// 		int[][] real_map = new int[6][4];
// 		int[][] red = {{1,1},{2,3}};
// 		int[][] box = {{5,0},{5,1}};
// 		int move_count = 0;
// 		int turn_count = 0;

// // 		nothing = 0, red = 1, box = 2
// 		for (int i = 0; i < red.length; i++) {
// 			int[] temp = red[i];
// 			real_map[temp[0]][temp[1]] = 1;
// 		}
// 		for (int i = 0; i < box.length; i++) {
// 			int[] temp = box[i];
// 			real_map[temp[0]][temp[1]] = 2;
// 		}
	// e #########################################################
		
		// answer
		ArrayList<int[]> red_found = new ArrayList<int[]>();
		ArrayList<int[]> box_found = new ArrayList<int[]>();
		
		// search map (BFS)
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
    		    
    		    if (found_num == 4 || (target_pos[0] == -1 && target_pos[1] == -1)) { // cannot find un-visited cells
    		        finished = true;
    		        path = find_path(map, current_pos, original_pos);
        		} else if (visited[target_pos[0]][target_pos[1]]) {
					break;
				} else {
            		path = find_path(map, current_pos, target_pos);
        		}
        		
				// s ###################################
        		// System.out.printf("current: %s, target: %s, path: %s\n",Arrays.toString(current_pos),Arrays.toString(target_pos), path.toString());
				// e ###################################
        		
        		//move along with calculated path
        		for (int way: path) {
        			if(testing) {
        				lcd.clear();
        				lcd.drawString("cur_pos: " + current_pos[0] + ", " + current_pos[1], 1, 4);
						Delay.msDelay(1000);
        			}
        		    // System.out.println("current_pos: " + Arrays.toString(current_pos) + "\n");
        		    // first check this is visited block;
    			    if (!finished && !visited[current_pos[0]][current_pos[1]]) {
    			        if (is_red()) { // check_red_block
    			            red_found.add(current_pos);
							found_num++;
    			        }
    			        visited[current_pos[0]][current_pos[1]] = true;
        			}
        			
					if (way != direction) {
						turn_robot(direction, way);
						direction = way;
					// s ###################################
						// turn_count++;
					// e ###################################
					}

        			int[] d = drdc[way];
    			    int[] new_pos = {current_pos[0] + d[0], current_pos[1] + d[1]};
    			    
    			    if (!finished && is_box()) {
    			        if (!visited[new_pos[0]][new_pos[1]]) {
                            box_found.add(new_pos);
							found_num++;
                            map[new_pos[0]][new_pos[1]] = 2;
                            visited[new_pos[0]][new_pos[1]] = true;
    			        }
    			        
    			        // while (!is_right_position(new_pos) || is_box(real_map, new_pos)) { // turn until no box in front of robot
        				// 	direction = (direction + 1) % 4; //turn right
						// 	right_turn();
						// // s ###################################
						// 	turn_count++;
						// // e ###################################
        				// 	//recalculate
        				// 	d = drdc[direction];
        				// 	new_pos = new int[] {current_pos[0] + d[0], current_pos[1] + d[1]};
    			        // }

						// move_forward();
					// s ###################################
						// current_pos = new_pos;
						// move_count++;
    			        // System.out.println("move_to: " + Arrays.toString(current_pos) + "\n");
					// e ###################################
    			        // visited[new_pos[0]][new_pos[1]] = true;
    			        break; // recalculate path

        		    } else {
						move_forward();
						current_pos = new_pos;
					// s ###################################
						// move_count++;
						// System.out.println("move_to: " + Arrays.toString(current_pos) + "\n");
					// e ###################################
        		    }
        		}
    		}
    		
    		if (finished) { // end moving
    		    break;
    		}
		} while(keys.getButtons() != Keys.ID_ESCAPE);
		
		// s ###################################
		// print function should be different
		// for (int[] item: red_found) {
		//     System.out.println("red_found: " + Arrays.toString(item) + "\n");
		// }
		// for (int[] item: box_found) {
		//     System.out.println("box_found: " + Arrays.toString(item) + "\n");
		// }
		// System.out.printf("move_count: %d, turn_count: %d\n",move_count, turn_count);
		// System.out.printf("current_pos: %s\n", Arrays.toString(current_pos));
		// e ###################################

		lcd.clear();
		
		int line_count = 0;
		for (int[] item: red_found) {
			lcd.drawString("(" + item[0] + "," + item[1] + "," + "R)", 0, line_count++);
		}
		for (int[] item: box_found) {
			lcd.drawString("(" + item[0] + "," + item[1] + "," + "B)", 0, line_count++);
		}
		do{
			Delay.msDelay(1000);
		} while(keys.getButtons() != Keys.ID_ESCAPE);
    }
}
