package search_algorithm;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Scanner;

public class search_algorithm {
	public static void main(String[] args) {
		int[][] drdc = { { 1, 0 }, { 0, 1 }, { -1, 0 }, { 0, -1 } };
		boolean[][] visited = new boolean[4][6];
		int[][] map = new int[4][6];
		int[] current_pos = {0,0};
		
		// for debugging, not used in real test
		// nothing = 0, red = 1, box = 2
		int[][] red = {};
		int[][] box = {};
		
		for (int i = 0; i < red.length; i++) {
			int[] temp = red[i];
			map[temp[0]][temp[1]] = 1;
		}
		for (int i = 0; i < box.length; i++) {
			int[] temp = box[i];
			map[temp[0]][temp[1]] = 2;
		}
		
		// answer
		ArrayList<int[]> red_found = new ArrayList<int[]>();
		ArrayList<int[]> box_found = new ArrayList<int[]>();
		
		int direction = 0; // 0 = up, 1 = right, 2 = down, 3 = left

		// move
		while (true) { //up
			visited[current_pos[0]][current_pos[1]] = true;
			
			// if floor is red
			if (map[current_pos[0]][current_pos[1]] == 1) {
				red_found.add(current_pos);
			}
			
			// select where to go
			if ((direction == 0 && current_pos[0] < 3) ||
					(direction == 2 && current_pos[0] > 0))	{ // go up
				direction += 0; // go straight
			}
			else if ((direction == 0 && current_pos[0] == 3) ||
					(direction == 2 && current_pos[0] == 0)){ // front or back is blocked
				direction += 1; // turn (up -> right), (down -> left)
			}
			else if (direction == 1 || direction == 3){
				direction = (direction + 1) % 4; // turn (right -> down), (left -> up)
			}
			
			int[] d = drdc[direction];
			int[] new_pos = {current_pos[0] + d[0], current_pos[1] + d[1]};
			
			if (map[new_pos[0]][new_pos[1]] == 2) {// front is blocked by block;
				box_found.add(new_pos);
				direction += 1; //turn (up -> right), (down -> left)
			}
			current_pos = new_pos;
			
		}
	}
}
