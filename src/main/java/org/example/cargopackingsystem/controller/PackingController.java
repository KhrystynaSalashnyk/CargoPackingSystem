package org.example.cargopackingsystem.controller;

import org.example.cargopackingsystem.model.Cargo;
import org.example.cargopackingsystem.model.GeneticOptimizer;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

@Controller
public class PackingController {

    private List<Cargo> cargoList = new ArrayList<>();
    // Ініціалізуємо об'єкт, щоб уникнути помилки "Cannot resolve symbol"
    private GeneticOptimizer optimizer = new GeneticOptimizer();

    private int containerW = 200;
    private int containerH = 200;

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("cargoList", cargoList);
        model.addAttribute("newCargo", new Cargo());
        model.addAttribute("containerW", containerW);
        model.addAttribute("containerH", containerH);

        // Розраховуємо фітнес для поточного списку
        double fitness = optimizer.calculateFitness(cargoList, containerW, containerH);
        model.addAttribute("fitness", String.format("%.2f%%", fitness * 100));

        return "index";
    }

    @PostMapping("/add-cargo")
    public String addCargo(@ModelAttribute Cargo newCargo, RedirectAttributes redirectAttributes) {
        // 1. Попередня перевірка за габаритами контейнера
        if (newCargo.getWidth() > containerW || newCargo.getHeight() > containerH) {
            redirectAttributes.addFlashAttribute("error", "Вантаж фізично більший за контейнер!");
            return "redirect:/";
        }

        // 2. Перевірка за сумарною площею
        int currentOccupiedArea = cargoList.stream().mapToInt(c -> c.getWidth() * c.getHeight()).sum();
        int newArea = newCargo.getWidth() * newCargo.getHeight();
        if (currentOccupiedArea + newArea > (containerW * containerH)) {
            redirectAttributes.addFlashAttribute("error", "Недостатньо вільної площі в контейнері!");
            return "redirect:/";
        }

        // 3. ГЕОМЕТРИЧНА ПЕРЕВІРКА: чи знайдеться місце для нового вантажу?
        List<Cargo> testList = new ArrayList<>(cargoList);
        testList.add(newCargo);

        // Скидаємо координати перед тестом
        newCargo.setX(-1);
        newCargo.setY(-1);

        optimizer.calculateFitness(testList, containerW, containerH);

        // Якщо після розрахунку координати залишилися від'ємними — місця немає
        if (newCargo.getX() < 0 || newCargo.getY() < 0) {
            redirectAttributes.addFlashAttribute("error", "Геометрично неможливо розмістити цей вантаж серед інших!");
            return "redirect:/";
        }

        // 4. Тільки якщо всі перевірки пройшли, додаємо в реальний список
        newCargo.setId(cargoList.size() + 1);
        cargoList.add(newCargo);
        return "redirect:/";
    }

    @PostMapping("/update-container")
    public String updateContainer(@RequestParam int width, @RequestParam int height) {
        if (width > 0 && height > 0) {
            this.containerW = width;
            this.containerH = height;
            // Очищуємо список, бо при зміні розмірів контейнера старе пакування неактуальне
            cargoList.clear();
        }
        return "redirect:/";
    }


    @PostMapping("/delete-cargo/{id}")
    public String deleteCargo(@PathVariable int id) {
        // 1. Видаляємо вантаж за його ID
        cargoList.removeIf(c -> c.getId() == id);

        // 2. ПЕРЕРАХУНОК ІНДЕКСІВ: щоб після видалення #2, колишній #3 став новим #2
        for (int i = 0; i < cargoList.size(); i++) {
            cargoList.get(i).setId(i + 1);
        }

        // 3. Оновлюємо координати для нового порядку
        optimizer.calculateFitness(cargoList, containerW, containerH);

        return "redirect:/";
    }
}