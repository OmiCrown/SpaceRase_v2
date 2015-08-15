package com.codenjoy.dojo.spacerace.client;

import java.util.List;

import com.codenjoy.dojo.client.Direction;
import com.codenjoy.dojo.client.Solver;
import com.codenjoy.dojo.client.WebSocketRunner;
import com.codenjoy.dojo.services.Dice;
import com.codenjoy.dojo.services.Point;
import com.codenjoy.dojo.services.PointImpl;
import com.codenjoy.dojo.services.RandomDice;
import com.codenjoy.dojo.spacerace.model.Elements;

/**
 * User: your name
 * Это твой алгоритм AI для игры. Реализуй его на свое усмотрение.
 * Обрати внимание на {@see YourSolverTest} - там приготовлен тестовый
 * фреймворк для тебя.
 */
public class YourSolver implements Solver<Board> {

    private static final String USER_NAME = "kudriavtsev.oleksii@gmail.com";

    private Dice dice;
    private Board board;
    private int bullets = 0;

    public YourSolver(Dice dice) {
        this.dice = dice;
    }

    public static void main(String[] args) {
        start(USER_NAME, WebSocketRunner.Host.REMOTE);
    }

    public static void start(String name, WebSocketRunner.Host server) {
        try {
            WebSocketRunner.run(server, name,
                    new YourSolver(new RandomDice()),
                    new Board());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String get(Board board) {
        this.board = board;
        if (board.isGameOver())
            return "";
        Direction result;
        result = findDirection();
        if (result != null) {
            if(isStoneOrBombAtop() & bullets > 0){
                if(isBulletAtop()){
                    return result.toString();
                }
                bullets--;
                return result + Direction.ACT.toString();
            }
            return result.toString();
        }
        return Direction.STOP.toString();
    }

    private boolean isBulletAtop() {
        int y = board.getMe().getY();
        int x = board.getMe().getX();

        for (int i = y - 1; i >= 0; i--) {
            if(board.isBulletAt(x,i)){
                return true;
            }
        }
        return false;
    }

    private boolean isStoneOrBombAtop() {
        int y = board.getMe().getY();
        int x = board.getMe().getX();

        for (int i = y - 1; i >= 0; i--) {
            if(board.isStoneAt(x,i) || board.isBombAt(x, i)){
                return true;
            }
        }
        return false;
    }

    private Direction findDirection() {
        Direction result = Direction.STOP;

        if (board.getMe() != null) {
            result = findDirectionToBulletPack();
        }else {
            System.out.println("Me not found!");
        }
        return CheckResult(result);
    }

    private Direction findDirectionToBulletPack() {
        Direction directionToBulletPack = Direction.STOP;
        Point me = board.getMe();
        Point box = getBulletPack();

        if (isAtLeastOneBulletPack(box)) {
            Point newMe;
            int x = me.getX();
            int y = me.getY();
            double newDistance = (double) Integer.MAX_VALUE;
			double distance;

            newMe = new PointImpl(x + 1, y);
			distance = newMe.distance(box);
			if (distance < newDistance) {
				newDistance = distance;
				directionToBulletPack = Direction.RIGHT;
			}

			newMe = new PointImpl(x, y + 1);
			distance = newMe.distance(box);
			if (distance < newDistance) {
				newDistance = distance;
				directionToBulletPack = Direction.DOWN;
			}

			newMe = new PointImpl(x - 1, y);
			distance = newMe.distance(box);
			if (distance < newDistance) {
				newDistance = distance;
				directionToBulletPack = Direction.LEFT;
			}

			newMe = new PointImpl(x, y - 1);
			distance = newMe.distance(box);
			if (distance < newDistance) {
				newDistance = distance;
				directionToBulletPack = Direction.UP;
			}

			rechargeBulletsWhenNeed(newDistance);
        }
        return directionToBulletPack;
    }

    private Point getBulletPack() {
        //todo найти ближайший, а не первый по списку (но можно рэндомом)
        //или вообще какую стратегию.. типа, где меньше игроков,
        // или самая низкая по у
        return board.get(Elements.BULLET_PACK).get(0);
    }

    private boolean isAtLeastOneBulletPack(Point box) {
        List<Point> boxes = board.get(Elements.BULLET_PACK);
        return boxes.size() != 0 && box != null;
    }

    private void rechargeBulletsWhenNeed(double newDistance) {
        if(newDistance < 1){
            bullets = 10;
        }
    }

    private Direction CheckResult(Direction result) {
        Direction checkedResultStone;
        Direction checkedResultBomb;
        Direction checkedDirection = Direction.STOP;

        Point me = board.getMe();
        if (me != null) {

            checkedResultStone = findBestDirectionNearStone(result);
            checkedResultBomb = findBestDirectionNearBomb(result);
//            checkedHighPosition

            if(checkedResultBomb.equals(result)){
                checkedDirection = checkedResultStone;
            }else {
                checkedDirection = checkedResultBomb;
            }
        }
        return checkedDirection;
    }

    private Direction findBestDirectionNearBomb(Direction givenDirection) {
        Direction bestDirection = givenDirection;
        int x = board.getMe().getX();
        int y = board.getMe().getY();

        // + проход навстречу
		if ((board.isBombAt(x, y - 4) || board.isBombAt(x, y - 3))
                & (bestDirection.equals(Direction.UP))) {
			if ((new PointImpl(x + 1, y)).distance(getBulletPack()) >
                    (new PointImpl(x - 1, y)).distance(getBulletPack())) {
				return Direction.LEFT;
			}
			return Direction.RIGHT;
		}

        // + если мина вверху в вдух ячейках - уходим вниз
        if ((board.isBombAt(x, y - 2))){
            return Direction.DOWN;
        }

        // + если мина вверху справа в соседней колонке и движимся вправо или вверх,
        // то на одну влево
        if ((board.isBombAt(x + 1, y - 3) || board.isBombAt(x + 1, y - 2) ) &
                (bestDirection.equals(Direction.RIGHT) ||
                        bestDirection.equals(Direction.UP))){
            return Direction.LEFT;
        }

        // если мина вверху справа в колонке через одну и движимся вправо, то ждем
        if ((board.isBombAt(x + 2, y - 2)) & // TODO implement directions
                (bestDirection.equals(Direction.RIGHT))){
            return Direction.STOP;
        }

        // еще ждем
        if ((board.isBombAt(x + 2, y - 1)) & // TODO implement directions
                (bestDirection.equals(Direction.RIGHT))){
            return Direction.STOP;
        }

        // еще ждем
        if ((board.isBombAt(x + 2, y)) & // TODO implement directions
                (bestDirection.equals(Direction.RIGHT))){
            return Direction.STOP;
        }

        // если мина вверху справа в колонке через одну и движимся вправо,
        // а мина уже прошла мимо,то идем дальше
        if ((board.isBombAt(x + 2, y + 1)) & // TODO implement directions
                (bestDirection.equals(Direction.RIGHT))){
            return Direction.RIGHT;
        }

        // если мина вверху слева в соседней колонке и движимся влево, то возврат на одну
        if ((board.isBombAt(x - 1, y - 3)) & // TODO implement directions
                (bestDirection.equals(Direction.LEFT) || bestDirection.equals(Direction.UP))) {
            return Direction.RIGHT;
        }

        // если мина вверху слева в соседней колонке и движимся влево, то возврат на одну
        if ((board.isBombAt(x - 1, y - 2)) & // TODO implement directions
                (bestDirection.equals(Direction.LEFT) || bestDirection.equals(Direction.UP))){
            return Direction.RIGHT;
        }

        // если мина вверху слева в колонке через одну и движимся влево, то ждем
        if ((board.isBombAt(x - 2, y - 2)) & // TODO implement directions
                (bestDirection.equals(Direction.LEFT))){
            return Direction.STOP;
        }

        // еще ждем
        if ((board.isBombAt(x - 2, y - 1)) & // TODO implement directions
                (bestDirection.equals(Direction.LEFT))){
            return Direction.STOP;
        }

        // еще ждем
        if ((board.isBombAt(x - 2, y)) & // TODO implement directions
                (bestDirection.equals(Direction.LEFT))){
            return Direction.STOP;
        }

        // если мина вверху слева в колонке через одну и движимся влево,
        // а мина уже прошла мимо,то идем дальше
        if ((board.isBombAt(x - 2, y + 1)) & // TODO implement directions
                (bestDirection.equals(Direction.LEFT))){
            return Direction.LEFT;
        }
        return bestDirection;
    }

    private Direction findBestDirectionNearStone(Direction givenDirection) {
        Direction bestDirection = givenDirection;
        int x = board.getMe().getX();
        int y = board.getMe().getY();

        if ((board.isStoneAt(x - 1, y - 1)) &
                (bestDirection.equals(Direction.LEFT))){
            return Direction.STOP;
        }

        if ((board.isStoneAt(x + 1, y - 1)) &
                (bestDirection.equals(Direction.RIGHT))){
            return Direction.STOP;
        }

        if (((board.isStoneAt(x, y - 1)) ||
                (board.isStoneAt(x, y - 2))) &
                (bestDirection.equals(Direction.UP))){
            return Direction.LEFT;
        }
        return bestDirection;
    }
}
