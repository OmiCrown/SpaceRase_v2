package com.codenjoy.dojo.spacerace.client;

import com.codenjoy.dojo.client.AbstractBoard;
import com.codenjoy.dojo.spacerace.model.Elements;
import com.codenjoy.dojo.services.Point;

/**
 * Класс, обрабатывающий строковое представление доски.
 * Содержит ряд унаследованных методов {@see AbstractBoard},
 * но ты можешь добавить сюда любые свои методы на их основе.
 */
public class Board extends AbstractBoard<Elements> {

    @Override
    public Elements valueOf(char ch) {
        return Elements.valueOf(ch);
    }

    public boolean isBarrierAt(int x, int y) {
        return isAt(x, y, Elements.WALL);
    }

    public Point getMe() {
        return get(Elements.DEAD_HERO,
                Elements.HERO).get(0);
    }

    public boolean isGameOver() {
        return !get(Elements.DEAD_HERO).isEmpty();
    }

    public boolean isBombAt(int x, int y) {
        return isAt(x, y, Elements.BOMB);
    }

    public boolean isStoneAt(int x, int y) {
        return isAt(x, y, Elements.STONE);
    }

    public boolean isBulletAt(int x, int y) {
        return isAt(x, y, Elements.BULLET);
    }
}