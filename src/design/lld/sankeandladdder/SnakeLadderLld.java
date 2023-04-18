/**
 * 
 */
package design.lld.sankeandladdder;

import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author 91978
 *
 */
public class SnakeLadderLld {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		Board board = new Board(10, 15, 15);
		
		Dice dice = new DiceImpl(1, 1, 6);
		
		Deque<Player> players = new LinkedList<>();
		players.add(new Player(1, 0));
		players.add(new Player(2, 0));
		
		WinStrategy strategy = new ExactWinStrategy();
		
		Game game = new Game(board, dice, players,strategy);
		
		game.startGame();
		
		System.out.println("Winner :"+game.getWinner().getId());
	}

}

class Player{
	
	private Integer id;
	private Integer curPos;
	
	public Player(Integer id, Integer curPos) {
		this.id = id;
		this.curPos = curPos;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getCurPos() {
		return curPos;
	}

	public void setCurPos(Integer curPos) {
		this.curPos = curPos;
	}

	
}

class Jump{
	
	private Integer start;
	private Integer end;
	
	public Jump(Integer start, Integer end) {
		this.start = start;
		this.end = end;
	}

	public Integer getStart() {
		return start;
	}

	public void setStart(Integer start) {
		this.start = start;
	}

	public Integer getEnd() {
		return end;
	}

	public void setEnd(Integer end) {
		this.end = end;
	}

	@Override
	public String toString() {
		return "Jump [start=" + start + ", end=" + end + "]";
	}
	
	
	
}

class Cell{
	
	private Jump jump;
	
	public Cell(Jump jump) {
		this.jump = jump;
	}

	public Jump getJump() {
		return jump;
	}

	public void setJump(Jump jump) {
		this.jump = jump;
	}

	@Override
	public String toString() {
		return "Cell [jump=" + jump + "]";
	}
	
	
	
}

interface Dice{
	
	int rollDice();
	
}

class DiceImpl implements Dice{

	private Integer diceCount;
	
	private Integer min;
	
	private Integer max;
	
	public DiceImpl(Integer diceCount, Integer min, Integer max) {
		
		this.diceCount = diceCount;
		this.min = min;
		this.max = max;
		
	}

	@Override
	public int rollDice() {
		
		int diceValueSum = 0;
		int diceCount = this.diceCount;
		
		while(diceCount-->0) {
			
			diceValueSum += ThreadLocalRandom.current().nextInt(min,max+1);
		}
		
		return diceValueSum;
	}
	
	
	
}

class Board{
	
	private Cell cells[][];
	private Integer size;
	private Integer snake;
	private Integer ladder;
	
	public Board(Integer size, Integer snake, Integer ladder) {
		
		this.size = size;
		this.snake = snake;
		this.ladder = ladder;
		
		initializeBoard();
		
	}

	private void initializeBoard() {
		
		this.cells = new Cell[this.size][this.size];
		
		for(int i=0;i<this.size;i++) {
			for(int j=0;j<this.size;j++) {
				cells[i][j] = new Cell(new Jump(0,0));
			}
		}
		
		Set<Integer> hs = new HashSet<>();
		
		while(snake>0) {
			
			int start = ThreadLocalRandom.current().nextInt(1,(this.size * this.size));
			int end = ThreadLocalRandom.current().nextInt(1,(this.size * this.size));
			
			if((start > end) && !hs.contains(start+end)) {
				hs.add(start+end);
				snake--;
				int startRow = start / 10; 
				int startCol = start % 10;
				
				this.cells[startRow][startCol] = new Cell(new Jump(start,end));
			}
			
		}
		
		hs.clear();
		
		while(ladder>0) {
			
			int start = ThreadLocalRandom.current().nextInt(1,(this.size * this.size)-1);
			int end = ThreadLocalRandom.current().nextInt(1,(this.size * this.size)-1);
			
			if((start < end) && !hs.contains(start+end)) {
				hs.add(start+end);
				ladder--;
				int startRow = start / 10; 
				int startCol = start % 10;
				
				this.cells[startRow][startCol] = new Cell(new Jump(start,end));
			}
			
		}
		
	}

	public Cell[][] getCells() {
		return cells;
	}

	public void setCells(Cell[][] cells) {
		this.cells = cells;
	}

	public Integer getSize() {
		return size;
	}

	public void setSize(Integer size) {
		this.size = size;
	}

	public Integer getSnake() {
		return snake;
	}

	public void setSnake(Integer snake) {
		this.snake = snake;
	}

	public Integer getLadder() {
		return ladder;
	}

	public void setLadder(Integer ladder) {
		this.ladder = ladder;
	}
	
	
}

class Game{
	
	private Board board;
	private Dice dice;
	private Deque<Player> players;
	private Player winner;
	
	private WinStrategy strategy;
	
	public Game(Board board, Dice dice, Deque<Player> players, WinStrategy strategy) {
		this.board = board;
		this.dice = dice;
		this.players = players;
		this.winner = null;
		this.strategy = strategy;
	}
	
	public void startGame() {
		
		while(winner == null) {
			
			Player p = players.removeFirst();
			players.addLast(p);
			
			int curPos = p.getCurPos();
			
			int diceValue = dice.rollDice();
			
			int finalPos = getFinalPos(diceValue);
			
			curPos += finalPos==0 ? diceValue :finalPos;
			
			p.setCurPos(curPos);
			
			
			//It has to be extensible define separet interface
			if(this.strategy.strategy(curPos, (this.board.getSize() * this.board.getSize())-1)) {
				this.winner = p;
				break;
			}
		}
		
	}

	private int getFinalPos(int diceValue) {
		
		int end = 0;
		
		int row = diceValue / 10; 
		int col = diceValue % 10;
		
		end = this.board.getCells()[row][col].getJump().getEnd();
		
		return end;
	}

	public Board getBoard() {
		return board;
	}

	public void setBoard(Board board) {
		this.board = board;
	}

	public Dice getDice() {
		return dice;
	}

	public void setDice(Dice dice) {
		this.dice = dice;
	}

	public Deque<Player> getPlayers() {
		return players;
	}

	public void setPlayers(Deque<Player> players) {
		this.players = players;
	}

	public Player getWinner() {
		return winner;
	}

	public void setWinner(Player winner) {
		this.winner = winner;
	}
	
	
	
}

interface WinStrategy{
	
	boolean strategy(int curPos, int endPos);
	
}

class ExactWinStrategy implements WinStrategy{

	@Override
	public boolean strategy(int curPos, int endPos) {
		
		return curPos==endPos;
	}

	
	
	
	
}

class GreaterWinStrategy implements WinStrategy{

	@Override
	public boolean strategy(int curPos, int endPos) {
		// TODO Auto-generated method stub
		return curPos>=endPos;
	}

	
	
	
	
}
