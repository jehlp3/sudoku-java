package model;

import java.util.Collection;
import java.util.List;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public class Board {

    private final List<List<Space>> spaces;

    public Board(List<List<Space>> spaces) {
        this.spaces = spaces;
    }

    public List<List<Space>> getSpaces() {
        return spaces;
    }

    public GameStatusEnum getStatus() {
        // Se não existe board, ou nenhuma célula não fixa, ainda é NON_STARTED
        boolean anyNonFixed = spaces.stream()
                .flatMap(Collection::stream)
                .anyMatch(s -> !s.isFixed());

        if (!anyNonFixed) {
            return GameStatusEnum.NON_STARTED; // não tem células editáveis
        }

        // Se pelo menos uma célula não fixa foi preenchida
        boolean anyEmpty = spaces.stream()
                .flatMap(Collection::stream)
                .anyMatch(s -> !s.isFixed() && isNull(s.getActual()));

        return anyEmpty ? GameStatusEnum.INCOMPLETE : GameStatusEnum.COMPLETE;
    }

    public boolean hasErrors() {
        if(getStatus() == GameStatusEnum.NON_STARTED) {
            return false;
        }

        return spaces.stream().flatMap(Collection::stream)
                .anyMatch(s -> nonNull(s.getActual()) && !s.getActual().equals(s.getExpected()));
    }

    public boolean changeValue(final int col, final int row, final int value) {
        var space = spaces.get(col).get(row);
        if(space.isFixed()) {
            return false;
        }

        space.setActual(value);
        return true;
    }

    public boolean clearValue(final int col, final int row) {
        var space = spaces.get(col).get(row);
        if(space.isFixed()) {
            return false;
        }

        space.clearSpace();
        return true;
    }

    public void reset() {
        spaces.forEach(c -> c.forEach(Space::clearSpace));
    }

    public boolean gameIsFinished() {
        return !hasErrors() && getStatus() == GameStatusEnum.COMPLETE;
    }

}