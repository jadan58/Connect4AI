package connect4;

import java.util.*;

public class Board {
	int board [][]=new int [6][7];
	
	//create a new board
	public Board() {
		//initially all slots are 0(empty)
		for(int i =0;i<6;i++)Arrays.fill(board[i], 0);
	}
	
	//check if move is valid inside the board (1 based)
	public boolean isValidMove(int col) {
		//out of bounds
		if(col<1 || col>7)return false;
		
		//if not full then move is valid
		if(board[0][col-1]==0)return true;
		
		return false;
	}
	
	
	//execute a drop action
	public boolean dropPiece(Move move) {
		//validations
		if(!isValidMove(move.col))return false;
		if(move.player!=1 && move.player !=2)return false;
		//execution
		for(int i =5;i>=0;i--) {
			if(board[i][move.col-1]==0) {
				board[i][move.col-1]=move.player;
				break;
			}
		}
		return true;
	}
	
	//check if the board is full
	public boolean isFull() {
		//if one top empty slot found => still not full 
		for(int i =0;i<7;i++) {
			if(board[0][i]==0)return false;
		}
		return true;
	}
	
	//get all valid option to drop piece
	public List<Integer> getValidCols(){
		List<Integer>valid = new ArrayList<>();
		for(int i =0;i<7;i++) {
			if(board[0][i]==0)valid.add(i+1);
		}
		return valid;
	}
	
	//check if a player won
	public boolean checkWin(int player) {
		for(int i=0;i<6;i++) {
			for(int j =0;j<7;j++) {
				//check right  
				if(checkRight(i,j,player))return true;
				//check left
				if(checkLeft(i,j,player))return true;
				//check down
				if(checkDown(i,j,player))return true;
				//check diagonal right
				if(checkDiagonalRight(i,j,player))return true;
				//check diagonal left
				if(checkDiagonalLeft(i,j,player))return true;
			}
		}
		return false;
	}
	public boolean checkRight(int i, int j, int player) {
		if(7-j<4)return false;
		if(board[i][j]!=player)return false;
		for(int x = j+1;x<j+4;x++) {
			if(board[i][x]!=board[i][x-1])return false;
		}
		return true;
	}
	
	public boolean checkLeft(int i, int j, int player) {
		if(j+1<4)return false;
		if(board[i][j]!=player)return false;
		for(int x = j-1;x>j-4;x--) {
			if(board[i][x]!=board[i][x+1])return false;
		}
		return true;
	}
	
	public boolean checkDown(int i, int j, int player) {
		if(6-i<4)return false;
		if(board[i][j]!=player)return false;
		for(int x = i+1;x<i+4;x++) {
			if(board[x][j]!=board[x-1][j])return false;
		}
		return true;
	}
	
	public boolean checkDiagonalRight(int i, int j, int player) {
		if(7-j<4 || 6-i<4)return false;
		if(board[i][j]!=player)return false;
		int x = i+1, y = j+1;
		for(int c = 1;c<4;c++) {
			if(board[x][y]!=board[x-1][y-1])return false;
			x++; y++;
		}
		return true;
	}
	
	public boolean checkDiagonalLeft(int i, int j, int player) {
		if(j+1<4 || 6-i<4)return false;
		if(board[i][j]!=player)return false;
		int x = i+1, y = j-1;
		for(int c = 1;c<4;c++) {
			if(board[x][y]!=board[x-1][y+1])return false;
			x++; y--;
		}
		return true;
	}
	
	//print the board
	public void printBoard() {
		for(int i =0;i<6;i++) {
			System.out.println(Arrays.toString(board[i]));
		}
	}

}
