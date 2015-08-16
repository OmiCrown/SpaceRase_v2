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
    Point bulletPack;
    private Point meAt;
    private int bulletsToCharge = 10;
    private  Point position;
    private Direction last;

    public YourSolver(Dice dice) {
        this.dice = dice;
    }

    public static void main(String[] args) {
//        LocalGameRunner.run(new GameRunner(), new YourSolver(new RandomDice()), new Board());
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
        position = getMe();
        if (result != null) {
            if(isStoneOrBombOrEnemyAtop() & bullets > 0){
                if(isBulletAtop()){
                    return result.toString();
                }
                bullets--;
                return result + Direction.ACT.toString();
            }
            last = result;
            return last.toString();
        }
        return last.toString();
    }

    private boolean isBulletAtop() {
        int y = getMe().getY();
        int x = getMe().getX();

        for (int i = y - 1; i >= 0; i--) {
            if(board.isBulletAt(x,i)){
                return true;
            }
        }
        return false;
    }

    private boolean isStoneOrBombOrEnemyAtop() {
        int y = getMe().getY();
        int x = getMe().getX();

        for (int i = y - 1; i >= 0; i--) {
            if(isEnemyAt(x, i) || isStoneAt(x, i) || isBombAt(x, i)){
                if(isEnemyAt(x, i)){
                    System.out.println("Enemy");
                }
                return true;
            }
        }
        return false;
    }

    private boolean isEnemyAt(int x, int y) {
        return board.isAt(x, y, Elements.OTHER_HERO);
    }

    private Direction findDirection() {
        Direction result = last; //todo возможно вниз вместо стоп будет получше?

        if (getMe() != null) {
            if(bullets < 2){
                result = findDirectionToBulletPack();
            }else{
                if(position.equals(getMe())){
                    result = Direction.random().clockwise();
                }else {
                    result = findDirectionToEnemy();
                }
            }
        }else {
            System.err.println("Me not found!");
        }
        return CheckResult(result);
    }

    private Direction findDirectionToEnemy() {
        Point enemy = getEnemy();
        Point me = getMe();
        Direction direction = last;
        PointImpl underEnemy = new PointImpl(enemy.getX(), enemy.getY() + 1);
        direction = getDirection(underEnemy, me, direction, false);
        return direction;
    }

    private Direction getDirection(Point enemy, Point me, Direction direction, boolean toRecharge) {
        if(enemy != null){
            Point newMe;
            int x = me.getX();
            int y = me.getY();
            double newDistance = (double) Integer.MAX_VALUE;
            double distance;

            newMe = new PointImpl(x + 1, y);
            distance = newMe.distance(enemy);
            if (distance < newDistance) {
                newDistance = distance;
                direction = Direction.RIGHT;
            }

            newMe = new PointImpl(x, y + 1);
            distance = newMe.distance(enemy);
            if (distance < newDistance) {
                newDistance = distance;
                direction = Direction.DOWN;
            }

            newMe = new PointImpl(x - 1, y);
            distance = newMe.distance(enemy);
            if (distance < newDistance) {
                newDistance = distance;
                direction = Direction.LEFT;
            }

            newMe = new PointImpl(x, y - 1);
            distance = newMe.distance(enemy);
            if (distance < newDistance) {
                newDistance = distance;
                direction = Direction.UP;
            }

//            if(!toRecharge){
//                if(getEnemy().getX() + 1 == getMe().getX() ||
//                        getEnemy().getX() - 1 == getMe().getX()){
//                    newDistance = 0.9;
//                    direction = Direction.DOWN;
//                }
//            }

            if(toRecharge){
                rechargeBulletsWhenNeed(newDistance);
            }
        }
        return direction;
    }

    private Point getEnemy() {
        int i = 0;
        Point enemy;  //todo найти ближайшего
//        do {
//            enemy = board.get(Elements.OTHER_HERO).get(i);
//            i++;
//        }while(enemy.getY() == (board.size() - 1) || i < board.get(Elements.OTHER_HERO).size() -1);
        if(board.get(Elements.OTHER_HERO).get(0).getY() == board.size() - 1){
            enemy = board.get(Elements.OTHER_HERO).get(1);
        }else {
            enemy = board.get(Elements.OTHER_HERO).get(0);
        }

        return enemy;
    }

    private Direction findDirectionToBulletPack() {
        Point box = getBulletPack();
        Point me = getMe();
        Direction direction = last;

        direction = getDirection(box, me, direction, true);
        return direction;
    }

    private Point getMe() {
        try{

            if(board.getMe() == null){
                Point me = new PointImpl(1, 1);
                return me;
            }
            meAt = board.getMe();
            return meAt;
        }catch (Exception e){
            System.err.print("board.getMe() - вызвал исключение");
        }
        return meAt;
    }

    private Point getBulletPack() {
        //todo найти ближайший, а не первый по списку (но можно рэндомом)
        //или вообще какую стратегию.. типа, где меньше игроков,
        // или самая низкая по у
        try{
            if(isAtLeastOneBulletPack()){
                Point b = board.get(Elements.BULLET_PACK).get(0);
                if (b != null){
                    return b;
                }
            }
            return bulletPack;
        }catch (Exception e){
            System.err.print("board.get(Elements.BULLET_PACK).get(0) - вызвал исключение");
        }
        return bulletPack;
    }

    private boolean isAtLeastOneBulletPack() {
        List<Point> boxes = board.get(Elements.BULLET_PACK);
        return (boxes.size() != 0);
    }

    private void rechargeBulletsWhenNeed(double newDistance) {
        if(newDistance < 1){
            bullets = bulletsToCharge;
        }
    }

    private Direction CheckResult(Direction result) {
        Direction checkedResultStone;
        Direction checkedResultBomb;
        Direction checkedDirection = last;
        Direction checkedBullets;

        Point me = getMe();
        if (me != null) {

            checkedResultStone = findBestDirectionNearStone(result);
            checkedResultBomb = findBestDirectionNearBomb(result);
            checkedBullets = checkedBullets(result);

            if(checkedResultBomb.equals(result)){
                if (checkedResultStone.equals(checkedResultBomb)){
                    if(checkedBullets.equals(result)){
                        checkedDirection = checkedBullets;
                    }
                }else {
                    checkedDirection = checkedResultStone;
                }
            }else {
                checkedDirection = checkedResultBomb;
            }
        }
        return checkedDirection;
    }

    private Direction checkedBullets(Direction result) {
        // моя опозиция + дирекшн + на одну вниз = пуля
        // то проверка стоп
        Point me = getMe();
        if ((result.equals(Direction.LEFT) & board.isBulletAt(me.getX() - 1, me.getY() - 1)) ||
        (result.equals(Direction.RIGHT) & board.isBulletAt(me.getX() + 1, me.getY() - 1))){
            return CheckResult(Direction.STOP);
        }else {
            return result;
        }
    }

    private Direction findBestDirectionNearBomb(Direction givenDirection) {
        Direction bestDirection = givenDirection;
        int x = getMe().getX();
        int y = getMe().getY();

        // + проход навстречу
		if ((isBombAt(x, y - 4) || isBombAt(x, y - 3)) & (isMovingUp(bestDirection))) {
            if (bullets > 1) {return Direction.ACT;}
            if (checkNewDistanceRight(x, y) > checkNewDistanceLeft(x, y)) {
				return Direction.LEFT;
			}
			return Direction.RIGHT;
		}

        // + если мина вверху в двух ячейках - уходим вниз
        if (isBombAt(x, y - 2)){
            return Direction.ACT.DOWN;
        }

        // + если мина вверху справа в соседней колонке и движимся вправо или вверх,
        // то на одну влево
        if ((isBombAt(x + 1, y - 3) || isBombAt(x + 1, y - 2)) &
                (isMovingRight(bestDirection) || isMovingUp(bestDirection))){
            return Direction.LEFT;
        }

        // если мина вверху справа в колонке через одну и движимся вправо, то ждем
        if ((isBombAt(x + 2, y - 2)) & // TODO test
                (isMovingRight(bestDirection))){
            return CheckResult(Direction.UP);
        }

        // еще ждем
        if ((isBombAt(x + 2, y - 1)) & // TODO implement directions
                (isMovingRight(bestDirection))){
            return Direction.STOP;
        }

        // еще ждем
        if ((isBombAt(x + 2, y)) & // TODO implement directions
                (isMovingRight(bestDirection))){
            return Direction.STOP;
        }

        // если мина вверху справа в колонке через одну и движимся вправо,
        // а мина уже прошла мимо,то идем дальше
        if ((isBombAt(x + 2, y + 1)) & // TODO implement directions
                (isMovingRight(bestDirection))){
            return Direction.RIGHT;
        }

        // если мина вверху слева в соседней колонке и движимся влево, то возврат на одну
        if ((isBombAt(x - 1, y - 3)) & // TODO implement directions
                (isMovingLeft(bestDirection) || isMovingUp(bestDirection))) {
            return Direction.RIGHT;
        }

        // если мина вверху слева в соседней колонке и движимся влево, то возврат на одну
        if ((isBombAt(x - 1, y - 2)) & // TODO implement directions
                (isMovingLeft(bestDirection) || isMovingUp(bestDirection))){
            return Direction.RIGHT;
        }

        // если мина вверху слева в колонке через одну и движимся влево, то ждем
        if ((isBombAt(x - 2, y - 2)) & // TODO implement directions
                (isMovingLeft(bestDirection))){
            return Direction.STOP;
        }

        // еще ждем
        if ((isBombAt(x - 2, y - 1)) & // TODO implement directions
                (isMovingLeft(bestDirection))){
            return Direction.STOP;
        }

        // еще ждем
        if ((isBombAt(x - 2, y)) & // TODO implement directions
                (isMovingLeft(bestDirection))){
            return Direction.STOP;
        }

        // если мина вверху слева в колонке через одну и движимся влево,
        // а мина уже прошла мимо,то идем дальше
        if ((isBombAt(x - 2, y + 1)) & // TODO implement directions
                (isMovingLeft(bestDirection))){
            return Direction.LEFT;
        }
        return bestDirection;
    }

    private double checkNewDistanceLeft(int x, int y) {
        return (new PointImpl(x - 1, y)).distance(getBulletPack());
    }

    private double checkNewDistanceRight(int x, int y) {
        return (new PointImpl(x + 1, y)).distance(getBulletPack());
    }

    private Direction findBestDirectionNearStone(Direction givenDirection) {
        Direction bestDirection = givenDirection;
        int x = getMe().getX();
        int y = getMe().getY();

        if (isStoneAt(x - 1, y - 1) & isMovingLeft(bestDirection)){
            return Direction.STOP;
        }

        if (isStoneAt(x + 1, y - 1) & isMovingRight(bestDirection)){
            return Direction.STOP;
        }

        if ((isStoneAt(x, y - 1) || isStoneAt(x, y - 2)) & isMovingUp(bestDirection)){
            if (bullets > 1) {return Direction.ACT;}
            if (checkNewDistanceRight(x, y) > checkNewDistanceLeft(x, y)) {
                return Direction.LEFT;
            }
            return Direction.RIGHT;  //todo check why always left, not right
        }
        return bestDirection;
    }

    private boolean isStoneAt(int x, int y) {
        return board.isStoneAt(x, y);
    }

    private boolean isBombAt(int x, int y) {
        return board.isBombAt(x, y);
    }

    private boolean isMovingUp(Direction bestDirection) {
        return bestDirection.equals(Direction.UP);
    }

    private boolean isMovingLeft(Direction bestDirection) {
        return bestDirection.equals(Direction.LEFT);
    }

    private boolean isMovingRight(Direction bestDirection) {
        return bestDirection.equals(Direction.RIGHT);
    }
}
