

import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import model.Board;
import model.GameStatusEnum;
import model.Space;
import util.BoardTemplate;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import java.util.ArrayList;
import java.util.List;

public class Main {

    private final static Scanner sc = new Scanner(System.in);
    private static Board board;
    private final static int BOARD_LIMIT = 9;

    public static void main(String[] args) {
        final var positions = Stream.of(args)
                .collect(Collectors.toMap(
                        k -> k.split(";")[0],
                        v -> v.split(";")[1]
                ));
        var option = -1;
        boolean firstMenuDisplay = true;

        while (true) {
            // Exibe apenas o menu
            System.out.println("\nSelecione uma das opções a seguir");
            System.out.println("1 - Iniciar um novo Jogo");
            System.out.println("2 - Colocar um novo número");
            System.out.println("3 - Remover um número");
            System.out.println("4 - limpar jogo");
            System.out.println("5 - Finalizar jogo");
            System.out.println("999 - Sair");

            option = sc.nextInt();

            // Processa a opção
            switch (option) {
                case 1 -> startGame(positions);
                case 2 -> inputNumber();
                case 3 -> removeNumber();
                case 4 -> clearGame();
                case 5 -> finishGame();
                case 999 -> System.exit(0);
                default -> System.out.println("Opção inválida, selecione uma das opções do menu");
            }

            // Mostrar o jogo **somente se já existir** e **não for NON_STARTED**
            if (nonNull(board) && board.getStatus() != GameStatusEnum.NON_STARTED) {
                showCurrentGame();
                showGameStatus();
            }

            firstMenuDisplay = false;
        }
    }



        private static void startGame(Map<String, String> positions) {
        if (nonNull(board)){
            System.out.println("O jogo já foi iniciado");
            return;
        }

        List<List<Space>> spaces = new ArrayList<>();
        for (int i = 0; i < BOARD_LIMIT; i++) {
            spaces.add(new ArrayList<>());
            for (int j = 0; j < BOARD_LIMIT; j++) {
                var positionConfig = positions.get("%s,%s".formatted(i, j));
                var expected = Integer.parseInt(positionConfig.split(",")[0]);
                var fixed = Boolean.parseBoolean(positionConfig.split(",")[1]);
                var currentSpace = new Space(expected, fixed);
                spaces.get(i).add(currentSpace);
            }
        }

        board = new Board(spaces);
        System.out.println("O jogo está pronto para começar");

    }

    private static void inputNumber() {
        if (isNull(board)){
            gameNotStarted();
            return;
        }


        System.out.println("Informe a coluna em que o numero será inserido: ");
        var col = runUntilGetValidNumber(0, 8);
        System.out.println("Informe a linha em que o numero será inserido: ");
        var row = runUntilGetValidNumber(0, 8);
        System.out.printf("Informe o número que vai entrar na posição [%s,%s]\n",col, row);
        var value = runUntilGetValidNumber(1, 9);

        if(!board.changeValue(col, row, value)) {
            System.out.printf("A posição [%s,%s] tem um valor fixo\n", col, row);
        }
    }

    private static void removeNumber() {
        if (isNull(board)){
            gameNotStarted();
            return;
        }

        System.out.println("Informe a coluna em que o numero será removido: ");
        var col = runUntilGetValidNumber(0, 8);
        System.out.println("Informe a linha em que o numero será removido: ");
        var row = runUntilGetValidNumber(0, 8);

        if(!board.clearValue(col, row)) {
            System.out.printf("A posição [%s,%s] tem um valor fixo\n", col, row);
        }

    }

    private static void showCurrentGame() {
        if (isNull(board)){
            gameNotStarted();
            return;
        }

        var args = new Object[81];
        var argPos = 0;
        var colSize = BOARD_LIMIT;
        for (int i = 0; i < BOARD_LIMIT; i++) {
            for(var col: board.getSpaces()) {
                args[argPos ++] = " " + (isNull(col.get(i).getActual()) ? " " : col.get(i).getActual());
            }
        }
        System.out.println("Seu jogo se encontra da seguinte forma:");
        System.out.printf((BoardTemplate.BOARD_TEMPLATE) + "%n", args);
    }

    private static void showGameStatus() {
        if (isNull(board)){
            gameNotStarted();
            return;
        }

        System.out.printf("O jogo atualmente se encontra no status: %s", board.getStatus().getLabel());
        System.out.println(board.hasErrors() ? "\nATENÇÃO ----> O jogo contém erros!" : "\nO jogo não contém erros!");

    }

    private static void clearGame() {
        if (isNull(board)){
            gameNotStarted();
            return;
        }

        System.out.println("Tem certeza que deseja limpar seu jogo e perder o progresso?");
        var confirm = sc.next();
        while(!confirm.equalsIgnoreCase("sim") && !confirm.equalsIgnoreCase("não")) {
            System.out.println("Informe 'sim' ou 'não'");
            confirm = sc.next();
        }

        if(confirm.equalsIgnoreCase("sim")) {
            board.reset();
        }
    }

    private static void finishGame() {
        if (isNull(board)){
            gameNotStarted();
            return;
        }

        if(board.gameIsFinished()) {
            System.out.println("Parabéns você concluiu o jogo");
            showCurrentGame();
            board = null;
        } else if (board.hasErrors()) {
            System.out.println("Seu jogo contém erros, verifique seu board e ajuste-o");
        } else {
            System.out.println("Você ainda precisa preencher algum espaço");
        }
    }

    private static int runUntilGetValidNumber(final int min, final int max) {
        var current = sc.nextInt();
        while (current < min || current > max) {
            System.out.printf("Informe um número  entre %s e %s", min, max);
            current = sc.nextInt();
        }
        return current;
    }

    private static void gameNotStarted(){
        System.out.println("ATENÇÃO ----> O jogo ainda não foi iniciado");
    }

    //0,0;5,true 1,0;3,true 2,0;0,false 3,0;0,false 4,0;7,true 5,0;0,false 6,0;0,false 7,0;0,false 8,0;0,false 0,1;6,true 1,1;0,false 2,1;0,false 3,1;1,true 4,1;9,true 5,1;5,true 6,1;0,false 7,1;0,false 8,1;0,false 0,2;0,false 1,2;9,true 2,2;8,true 3,2;0,false 4,2;0,false 5,2;0,false 6,2;0,false 7,2;6,true 8,2;0,false 0,3;8,true 1,3;0,false 2,3;0,false 3,3;0,false 4,3;6,true 5,3;0,false 6,3;0,false 7,3;0,false 8,3;3,true 0,4;4,true 1,4;0,false 2,4;0,false 3,4;8,true 4,4;0,false 5,4;3,true 6,4;0,false 7,4;0,false 8,4;1,true 0,5;7,true 1,5;0,false 2,5;0,false 3,5;0,false 4,5;2,true 5,5;0,false 6,5;0,false 7,5;0,false 8,5;6,true 0,6;0,false 1,6;6,true 2,6;0,false 3,6;0,false 4,6;0,false 5,6;0,false 6,6;2,true 7,6;8,true 8,6;0,false 0,7;0,false 1,7;0,false 2,7;0,false 3,7;4,true 4,7;1,true 5,7;9,true 6,7;0,false 7,7;0,false 8,7;5,true 0,8;0,false 1,8;0,false 2,8;0,false 3,8;0,false 4,8;8,true 5,8;0,false 6,8;0,false 7,8;7,true 8,8;9,true
    //0,0;5,true 1,0;3,true 2,0;4,false 3,0;6,true 4,0;7,true 5,0;8,true 6,0;9,false 7,0;1,true 8,0;2,true 0,1;6,true 1,1;7,false 2,1;2,true 3,1;1,true 4,1;9,true 5,1;5,true 6,1;3,true 7,1;4,false 8,1;8,true 0,2;1,true 1,2;9,true 2,2;8,true 3,2;3,false 4,2;4,true 5,2;2,true 6,2;5,true 7,2;6,true 8,2;7,true 0,3;8,true 1,3;5,true 2,3;9,true 3,3;7,false 4,3;6,true 5,3;1,false 6,3;4,true 7,3;2,true 8,3;3,true 0,4;4,true 1,4;2,true 2,4;6,true 3,4;8,true 4,4;5,true 5,4;3,true 6,4;7,true 7,4;9,true 8,4;1,true 0,5;7,true 1,5;1,false 2,5;3,true 3,5;9,true 4,5;2,true 5,5;4,true 6,5;8,true 7,5;5,false 8,5;6,true 0,6;9,true 1,6;6,true 2,6;1,true 3,6;5,true 4,6;3,true 5,6;7,true 6,6;2,false 7,6;8,true 8,6;4,true 0,7;2,true 1,7;8,true 2,7;7,true 3,7;4,true 4,7;1,true 5,7;9,true 6,7;6,true 7,7;3,true 8,7;5,true 0,8;3,true 1,8;4,true 2,8;5,true 3,8;2,false 4,8;8,true 5,8;6,true 6,8;1,true 7,8;7,true 8,8;9,true
}

