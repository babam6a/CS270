package search_algorithm;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Collections;

public class search_algorithm {
	public static int left_turn(int direction) {
        switch (direction) {
            case 0: // up
                return 3;
            case 1: // right
                return 0;
            case 2: // down
                return 1;
            case 3: // left
                return 2;
        }
        return -1;
    }
    public static int right_turn(int direction) {
        switch (direction) {
            case 0: // up
                return 1;
            case 1: // right
                return 2;
            case 2: // down
                return 3;
            case 3: // left
                return 0;
        }
        return -1;
    }
    
    public static ArrayList<Integer> find_path(int[] current_pos, int[] target_pos) {
        int dx = target_pos[0] - current_pos[0];
        int dy = target_pos[1] - current_pos[1];
        ArrayList<Integer> movement = new ArrayList<Integer>();
        
        for (int i = 0; i < Math.abs(dy); i++) {
            if (dy < 0) {
                movement.add(2); // down
            } else {
                movement.add(0); // up
            }
        }
        for (int i = 0; i < Math.abs(dx); i++) {
            if (dx < 0) {
                movement.add(3); // left
            } else {
                movement.add(1); // right
            }
        }
        
        Collections.shuffle(movement); // shuffle movement to avoid infinite loop
        return movement;
    }
    
    public static int[] find_nearest_pos(int[] current_pos, boolean[][] visited) {
        int radius = 0;
        int curr_x = current_pos[0];
        int curr_y = current_pos[1];
        
        int[] nearest_pos;
        
        while (true) {
            radius++;
            for (int i = curr_x - radius; i <= curr_x + radius; i++) {
				for (int j = curr_y - radius; j <= curr_y + radius; j++) {
					if (i == curr_x - radius || i == curr_x + radius || j == curr_y - radius || j == curr_y + radius) {
						if (is_right_position(new int[]{i, j}) && !visited[i][j]) {
							nearest_pos = new int[]{i, j};
							return nearest_pos;
						}
					}
				}
			}
            if (radius >= 5) { // all cells are visited
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

	public static boolean is_box(int[][] map, int[] position) {
		if (map[position[0]][position[1]] == 2) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean is_red(int[][] map, int[] position) {
		if (map[position[0]][position[1]] == 1) {
			return true;
		} else {
			return false;
		}
	}
    
    public static void main(String[] args) {
        int[][] drdc = { { 0, 1 }, { 1, 0 }, { 0, -1 }, { -1, 0 } };
		boolean[][] visited = new boolean[6][4];
		int[] original_pos = {0,0};
		int[] current_pos = original_pos;
		
	// #########################################################
		// for debugging. not used in real test
		// nothing = 0, red = 1, box = 2
		int[][] map = new int[6][4]; //x, y
		int[][] red = {{0,2},{5,1}};
		int[][] box = {{2,0},{4,3}};
		int move_count = 0;
		int turn_count = 0;
		
		for (int i = 0; i < red.length; i++) {
			int[] temp = red[i];
			map[temp[0]][temp[1]] = 1;
		}
		for (int i = 0; i < box.length; i++) {
			int[] temp = box[i];
			map[temp[0]][temp[1]] = 2;
		}
	// #########################################################
		
		// answer
		ArrayList<int[]> red_found = new ArrayList<int[]>();
		ArrayList<int[]> box_found = new ArrayList<int[]>();
		
		int direction = 0; // 0 = up, 1 = right, 2 = down, 3 = left
		
		// search map (BFS)
		boolean finished = false;
		
		// moving until visit all cells
		while (true) {
    		int[] target_pos = find_nearest_pos(current_pos, visited);
    		
    		while (current_pos[0] != target_pos[0] || current_pos[1] != target_pos[1]) {
    		    // calculate path from current_position to target_position
    		    ArrayList<Integer> path;

				if (current_pos[0] == original_pos[0] && current_pos[1] == original_pos[1] && finished) {
					break;
				}
    		    
    		    if (target_pos[0] == -1 && target_pos[1] == -1) { // cannot find un-visited cells
    		        finished = true;
    		        path = find_path(current_pos, original_pos);
        		} else if (visited[target_pos[0]][target_pos[1]]) {
					break;
				} else {
            		path = find_path(current_pos, target_pos);
        		}
        		
        		System.out.printf("current: %s, target: %s, path: %s\n",Arrays.toString(current_pos),Arrays.toString(target_pos), path.toString());
        		
        		//move along with calculated path
        		for (int way: path) {
        		    System.out.println("current_pos: " + Arrays.toString(current_pos) + "\n");
        		    // first check this is visited block;
    			    if (!visited[current_pos[0]][current_pos[1]]) {
    			        if (is_red(map, current_pos)) { // check_red_block
    			            red_found.add(current_pos);
    			        }
    			        visited[current_pos[0]][current_pos[1]] = true;
        			}
        			
					if (way != direction) {
						turn_count++;
					}
        			int[] d = drdc[way];
    			    int[] new_pos = {current_pos[0] + d[0], current_pos[1] + d[1]};
    			    
    			    if (is_box(map, new_pos)) {
    			        if (!visited[new_pos[0]][new_pos[1]]) {
                            box_found.add(new_pos);
                            visited[new_pos[0]][new_pos[1]] = true;
    			        }
    			        
    			        while (!is_right_position(new_pos) || is_box(map, new_pos)) { // turn until no box in front of robot
        					way = (way + 1) % 4; //turn (up -> right), (down -> left)
							turn_count++;
        					//recalculate
        					d = drdc[way];
        					new_pos = new int[] {current_pos[0] + d[0], current_pos[1] + d[1]};
    			        }
    			        current_pos = new_pos;
						move_count++;
    			        System.out.println("move_to: " + Arrays.toString(current_pos) + "\n");
    			        visited[new_pos[0]][new_pos[1]] = true;
    			        break; // recalculate path
        		    } else {
        		        current_pos = new_pos;
						move_count++;
						System.out.println("move_to: " + Arrays.toString(current_pos) + "\n");
        		    }
        		}
    		}
    		
    		if (finished) { // end moving
    		    break;
    		}
		}
		
		
		for (int[] item: red_found) {
		    System.out.println("red_found: " + Arrays.toString(item) + "\n");
		}
		for (int[] item: box_found) {
		    System.out.println("box_found: " + Arrays.toString(item) + "\n");
		}
		System.out.printf("move_count: %d, turn_count: %d\n",move_count, turn_count);
		System.out.printf("current_pos: %s\n", Arrays.toString(current_pos));
    }
}
