import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Зчитуємо множник від користувача
        System.out.print("Введіть множник: ");
        int multiplier = scanner.nextInt();

        // Генеруємо масив з рандомними числами (від 40 до 60 елементів)
        int arraySize = new Random().nextInt(21) + 40;
        List<Integer> numbers = generateRandomArray(arraySize);

        //Collections.sort(numbers);

        System.out.println("Згенерований масив: " + numbers);

        long start = System.currentTimeMillis();

        // Розділяємо масив на чотири частини
        List<Integer> part1 = new CopyOnWriteArrayList<>();
        List<Integer> part2 = new CopyOnWriteArrayList<>();
        List<Integer> part3 = new CopyOnWriteArrayList<>();
        List<Integer> part4 = new CopyOnWriteArrayList<>();

        // Заповнення частин відповідно до діапазону
        for (int num : numbers) {
            if (num >= -100 && num <= -50) {
                part1.add(num);
            } else if (num >= -49 && num <= 0) {
                part2.add(num);
            } else if (num >= 1 && num <= 50) {
                part3.add(num);
            } else if (num >= 51 && num <= 100) {
                part4.add(num);
            }
        }

        // Створюємо ExecutorService з фіксованим пулом з 4 потоків
        ExecutorService executor = Executors.newFixedThreadPool(4);

        // Створюємо список Future для асинхронного виконання задач
        List<Future<List<Integer>>> futures = new CopyOnWriteArrayList<>();

        // Додаємо Callable до виконання
        futures.add(executor.submit(createCallable(part1, multiplier)));
        futures.add(executor.submit(createCallable(part2, multiplier)));
        futures.add(executor.submit(createCallable(part3, multiplier)));
        futures.add(executor.submit(createCallable(part4, multiplier)));

        // Отримуємо результати та збираємо у фінальний масив
        List<Integer> result = new CopyOnWriteArrayList<>();

        for (int i = 0; i < futures.size(); i++) {
            Future<List<Integer>> future = futures.get(i);
            if (future.isCancelled()) {
                System.out.println("Задача " + (i + 1) + " була відмінена.");
                continue;
            }
            try {
                if (future.isDone()) {
                    List<Integer> partResult = future.get();
                    System.out.println("Проміжний результат для частини " + (i + 1) + ": " + partResult);
                    result.addAll(partResult);
                }
            } catch (InterruptedException | ExecutionException e) {
                System.out.println("Помилка під час отримання результату: " + e.getMessage());
            }
        }

        // Завершуємо роботу ExecutorService
        executor.shutdown();

        System.out.println("Результат обробки: " + result);
        System.out.println("Час роботи програми: " + (System.currentTimeMillis() - start) + " мс");
    }

    // Метод для генерації CopyOnWriteArrayList з рандомними числами
    private static List<Integer> generateRandomArray(int size) {
        List<Integer> list = new CopyOnWriteArrayList<>();
        Random random = new Random();
        for (int i = 0; i < size; i++) {
            list.add(random.nextInt(201) - 100); // Генерація чисел у діапазоні [-100; 100]
        }
        return list;
    }

    // Метод для створення Callable задачі для множення елементів
    public static Callable<List<Integer>> createCallable(List<Integer> part, int multiplier) {
        return () -> {
            List<Integer> result = new CopyOnWriteArrayList<>();
            for (Integer num : part) {
                result.add(num * multiplier);
            }
            return result;
        };
    }
}
