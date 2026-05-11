package org.example.cargopackingsystem.model;

import java.util.ArrayList;
import java.util.List;

public class GeneticOptimizer {

    public double calculateFitness(List<Cargo> arrangement, int containerW, int containerH) {
        if (containerW <= 0 || containerH <= 0 || arrangement.isEmpty()) return 0;

        List<Cargo> placedCargoes = new ArrayList<>();
        int usedArea = 0;

        for (Cargo cargo : arrangement) {
            boolean placed = false;
            // Шукаємо координати y та x (починаємо з 0)
            for (int y = 0; y <= containerH - cargo.getHeight(); y++) {
                for (int x = 0; x <= containerW - cargo.getWidth(); x++) {
                    // ПЕРЕВІРКА: чи не перетинається новий ящик з уже поставленими
                    if (canPlace(cargo, x, y, placedCargoes)) {
                        // ВАЖЛИВО: Оновлюємо реальні координати об'єкта в списку
                        cargo.setX(x);
                        cargo.setY(y);

                        // Створюємо копію або додаємо в список "зайнятих", щоб наступний ящик його бачив
                        placedCargoes.add(cargo);
                        usedArea += cargo.getArea();
                        placed = true;
                        break;
                    }
                }
                if (placed) break;
            }

            // Якщо ящик не вліз нікуди — ставимо йому координати поза межами, щоб не малювати
            if (!placed) {
                cargo.setX(-1000);
                cargo.setY(-1000);
            }
        }

        return (double) usedArea / (containerW * containerH);
    }
    private boolean canPlace(Cargo nc, int nx, int ny, List<Cargo> placed) {
        // nc - новий вантаж, nx/ny - координати, куди хочемо поставити
        for (Cargo pc : placed) {
            // Умова перетину прямокутників:
            // Вантажі накладаються, якщо виконуються ВСІ 4 умови одночасно:
            boolean overlapX = nx < pc.getX() + pc.getWidth() && nx + nc.getWidth() > pc.getX();
            boolean overlapY = ny < pc.getY() + pc.getHeight() && ny + nc.getHeight() > pc.getY();

            if (overlapX && overlapY) {
                return false; // Місце зайняте іншим вантажем!
            }
        }
        return true; // Місце вільне
    }
}