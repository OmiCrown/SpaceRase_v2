package com.codenjoy.dojo.spacerace.client.ai;

import java.util.List;

import com.codenjoy.dojo.client.Direction;
import com.codenjoy.dojo.client.LocalGameRunner;
import com.codenjoy.dojo.client.Solver;
import com.codenjoy.dojo.client.WebSocketRunner;
import com.codenjoy.dojo.services.Dice;
import com.codenjoy.dojo.services.Point;
import com.codenjoy.dojo.services.PointImpl;
import com.codenjoy.dojo.services.RandomDice;
import com.codenjoy.dojo.spacerace.client.Board;
import com.codenjoy.dojo.spacerace.model.Elements;
import com.codenjoy.dojo.spacerace.services.GameRunner;

public class AlAnTestSolver implements Solver<Board> {

	private int delay = 0;
	private boolean vpravo = true;
	private Board board;

	public AlAnTestSolver(Dice dice) {
	}

	/**
	 * Метод для запуска игры с текущим ботом. Служит для отладки.
	 */
	public static void main(String[] args) {
		LocalGameRunner.run(new GameRunner(), new AlAnTestSolver(new RandomDice()), new Board());
		// start(WebSocketRunner.DEFAULT_USER, WebSocketRunner.Host.LOCAL);
	}

	public static void start(String name, WebSocketRunner.Host server) {
		try {
			WebSocketRunner.run(server, name, new AlAnTestSolver(new RandomDice()), new Board());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public String get(final Board board) {
		this.board = board;
		if (board.isGameOver())
			return "";
		String result = "";
		result = findDirection(board);
		if (result != null) {
			return result;
		}
		return Direction.STOP.toString();

	}

	private String findDirection(Board board) {
		Direction result = Direction.STOP;

		Point me = board.getMe();
		if (me != null) {
			int x = me.getX();
			int y = me.getY();
			result = findDirectionToBulletPack(board, me, result);
		}
		return CheckResult(result, board);
	}

	private Direction findDirectionToBulletPack(Board board, Point me, Direction result) {
		List<Point> boxes = board.get(Elements.BULLET_PACK);
		if (boxes.size() != 0) {
			Point box = boxes.get(0);
			if (box != null) {
				Point newMe;
				double newDistance = (double) Integer.MAX_VALUE;
				double distance;
				newMe = new PointImpl(me.getX() + 1, me.getY());

				distance = newMe.distance(box);
				if (distance < newDistance) {
					newDistance = distance;
					result = Direction.RIGHT;
				}

				newMe = new PointImpl(me.getX(), me.getY() + 1);
				distance = newMe.distance(box);
				if (distance < newDistance) {
					newDistance = distance;
					result = Direction.DOWN;
				}

				newMe = new PointImpl(me.getX() - 1, me.getY());
				distance = newMe.distance(box);
				if (distance < newDistance) {
					newDistance = distance;
					result = Direction.LEFT;
				}

				newMe = new PointImpl(me.getX(), me.getY() - 1);
				distance = newMe.distance(box);
				if (distance < newDistance) {
					newDistance = distance;
					result = Direction.UP;
				}
			}
		}
		return result;
	}

	private String CheckResult(Direction result, Board board) {
		String checkedResult = result.toString();
		Point me = board.getMe();
		if (me != null) {
			int x = me.getX();
			int y = me.getY();

            checkedResult = findBestDirectionNearStone(board, me, result).toString();
		}
		return checkedResult;
	}

	private Direction findBestDirectionNearStone(Board board, Point me, Direction givenDirection) {
        Direction bestDirection = givenDirection;

        if ((board.isStoneAt(me.getX() - 1, me.getY() - 1)) & (bestDirection.equals(Direction.LEFT))){
            return Direction.STOP;
        }

        if ((board.isStoneAt(me.getX() + 1, me.getY() - 1)) & (bestDirection.equals(Direction.RIGHT))){
            return Direction.STOP;
        }

        if ((board.isStoneAt(me.getX(), me.getY() - 1)) & (bestDirection.equals(Direction.UP))){

//            if ((findBestDirectionNearStone(board, me, Direction.LEFT)).equals(bestDirection)){
                return Direction.LEFT;
//            }else {
//                return Direction.RIGHT;
//            }

//            return bestDirection;
        }


        return bestDirection;
    }

}
