package com.example.mybignumber.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

@Controller
public class BigNumberController {

    private static final Logger logger = Logger.getLogger(BigNumberController.class.getName());

    @Value("${app.core.path:./mybignumber_core}")
    private String corePath;

    /**
     * GET / — Display the input form (no result panel).
     */
    @GetMapping("/")
    public String index() {
        return "index";
    }

    /**
     * POST /calculate — Run the C++ core, parse history.log, and display results.
     */
    @PostMapping("/calculate")
    public String calculate(
            @RequestParam("num1") String num1,
            @RequestParam("num2") String num2,
            Model model) {

        // Echo inputs back to the form
        model.addAttribute("num1", num1);
        model.addAttribute("num2", num2);

        // --- Input Validation ---
        if (num1 == null || num1.isBlank() || num2 == null || num2.isBlank()) {
            model.addAttribute("error", "Vui lòng nhập cả hai số.");
            return "index";
        }

        // Only allow digits (no negative, no decimals, no letters)
        if (!num1.matches("\\d+") || !num2.matches("\\d+")) {
            model.addAttribute("error", "Chỉ được nhập số nguyên dương (các chữ số 0-9).");
            return "index";
        }

        // Resolve the executable and log file paths
        File coreExecutable = new File(corePath).getAbsoluteFile();
        File workingDirectory = coreExecutable.getParentFile();
        File historyLog = new File(workingDirectory, "history.log");

        // --- Pre-execution: Validate executable exists and is runnable ---
        if (!coreExecutable.exists()) {
            logger.severe("Core executable not found: " + coreExecutable.getAbsolutePath());
            model.addAttribute("error",
                    "Không tìm thấy file thực thi mybignumber_core. Vui lòng kiểm tra lại cấu hình.");
            return "index";
        }

        if (!coreExecutable.canExecute()) {
            logger.severe("Core executable is not executable: " + coreExecutable.getAbsolutePath());
            model.addAttribute("error",
                    "File mybignumber_core không có quyền thực thi. Hãy chạy: chmod +x mybignumber_core");
            return "index";
        }

        // --- Pre-execution: Delete old history.log to get a clean result ---
        try {
            Files.deleteIfExists(historyLog.toPath());
        } catch (IOException e) {
            logger.log(Level.WARNING, "Could not delete old history.log", e);
            // Non-fatal: the C++ code appends, so worst case we get extra data
        }

        // --- Execute the C++ process ---
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(
                    coreExecutable.getAbsolutePath(), num1, num2);
            processBuilder.directory(workingDirectory);
            processBuilder.redirectErrorStream(true);

            Process process = processBuilder.start();

            // Drain stdout to prevent blocking on large output
            String processOutput;
            try (BufferedReader reader = new BufferedReader(
                    new java.io.InputStreamReader(process.getInputStream()))) {
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append("\n");
                }
                processOutput = sb.toString();
            }

            boolean finished = process.waitFor(30, TimeUnit.SECONDS);

            if (!finished) {
                process.destroyForcibly();
                logger.severe("C++ process timed out after 30 seconds");
                model.addAttribute("error", "Quá trình tính toán vượt quá thời gian cho phép (30 giây).");
                return "index";
            }

            int exitCode = process.exitValue();
            if (exitCode != 0) {
                logger.severe("C++ process exited with code " + exitCode + ". Output: " + processOutput);
                model.addAttribute("error",
                        "Lỗi khi thực thi mybignumber_core (exit code: " + exitCode + ").");
                return "index";
            }

        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to execute C++ process", e);
            model.addAttribute("error", "Không thể khởi chạy mybignumber_core: " + e.getMessage());
            return "index";
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.log(Level.SEVERE, "Process was interrupted", e);
            model.addAttribute("error", "Quá trình tính toán bị gián đoạn.");
            return "index";
        }

        // --- Post-execution: Read and parse history.log ---
        if (!historyLog.exists()) {
            logger.severe("history.log was not created at: " + historyLog.getAbsolutePath());
            model.addAttribute("error",
                    "File history.log không được tạo sau khi thực thi. Kiểm tra lại mybignumber_core.");
            return "index";
        }

        try {
            List<String> steps = new ArrayList<>();
            String result = null;
            String operation = null;

            try (BufferedReader reader = new BufferedReader(new FileReader(historyLog))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    line = line.trim();

                    if (line.startsWith("=== Phep toan:")) {
                        // Extract operation header, e.g. "123 + 456"
                        operation = line.replace("=== Phep toan:", "")
                                        .replace("===", "").trim();
                    } else if (line.startsWith("Buoc ")) {
                        steps.add(line);
                    } else if (line.startsWith("Ket qua cuoi cung:")) {
                        result = line.replace("Ket qua cuoi cung:", "").trim();
                    }
                    // Ignore separator lines "---..."
                }
            }

            if (result == null) {
                model.addAttribute("error",
                        "Không tìm thấy kết quả trong history.log. File có thể bị hỏng.");
                return "index";
            }

            model.addAttribute("operation", operation);
            model.addAttribute("result", result);
            model.addAttribute("steps", steps);
            model.addAttribute("calculated", true);

        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to read history.log", e);
            model.addAttribute("error", "Không thể đọc file history.log: " + e.getMessage());
            return "index";
        }

        return "index";
    }
}
