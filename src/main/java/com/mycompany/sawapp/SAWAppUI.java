package com.mycompany.sawapp;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class SAWAppUI extends JFrame {
    private JTextField[][] decisionFields;
    private JTextField[] weightFields;
    private JCheckBox[] isBenefitCheckboxes;
    private JTextArea resultArea;

    public SAWAppUI() {
        setTitle("SAW Method Application");
        setSize(600, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Panel input dengan GridBagLayout
        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // Spasi antar komponen
        gbc.fill = GridBagConstraints.HORIZONTAL;

        decisionFields = new JTextField[3][3];
        weightFields = new JTextField[3];
        isBenefitCheckboxes = new JCheckBox[3];

        // Membuat label dan input untuk setiap alternatif dan kriteria
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                gbc.gridx = j;
                gbc.gridy = i * 2;
                inputPanel.add(new JLabel("Alt " + (i + 1) + " K" + (j + 1)), gbc);
                
                decisionFields[i][j] = new JTextField(5);
                gbc.gridx = j;
                gbc.gridy = i * 2 + 1;
                inputPanel.add(decisionFields[i][j], gbc);
            }

            // Menambahkan bobot dan checkbox Benefit
            gbc.gridx = 3;
            gbc.gridy = i * 2;
            inputPanel.add(new JLabel("Weight K" + (i + 1)), gbc);

            weightFields[i] = new JTextField(5);
            gbc.gridx = 3;
            gbc.gridy = i * 2 + 1;
            inputPanel.add(weightFields[i], gbc);

            isBenefitCheckboxes[i] = new JCheckBox("Benefit?");
            gbc.gridx = 4;
            gbc.gridy = i * 2 + 1;
            inputPanel.add(isBenefitCheckboxes[i], gbc);
        }

        // Tombol untuk menghitung SAW
        JButton calculateButton = new JButton("Calculate SAW");
        calculateButton.addActionListener(e -> calculateSAW());
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 5;
        inputPanel.add(calculateButton, gbc);

        // Tombol untuk bantuan
        JButton helpButton = new JButton("Help");
        helpButton.addActionListener(e -> showHelpPopup());
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 5;
        gbc.anchor = GridBagConstraints.SOUTHWEST;
        inputPanel.add(helpButton, gbc);

        // Panel hasil
        JPanel resultPanel = new JPanel();
        resultArea = new JTextArea(10, 30); // Ukuran 10 baris, 30 kolom
        resultArea.setEditable(false);
        resultArea.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        JScrollPane scrollPane = new JScrollPane(resultArea);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        // Mengatur ukuran scrollbar agar tetap sesuai
        scrollPane.setPreferredSize(new Dimension(400, 200)); // Ukuran sedang default
        scrollPane.setMaximumSize(new Dimension(400, 200)); // Ukuran maksimal tetap
        scrollPane.setMinimumSize(new Dimension(300, 150)); // Ukuran minimal tetap

        resultPanel.setLayout(new BorderLayout());
        resultPanel.add(scrollPane, BorderLayout.CENTER);

        // Menambahkan panel input dan hasil ke frame
        setLayout(new BorderLayout());
        add(inputPanel, BorderLayout.NORTH);
        add(resultPanel, BorderLayout.CENTER);

        // Listener untuk ukuran frame
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                // Jika ukuran berubah, tetap pertahankan ukuran scrollPane agar tidak terlalu besar
                Dimension newSize = getContentPane().getSize();
                int newWidth = Math.min(newSize.width - 50, 400); // Batasi lebar
                int newHeight = Math.min(newSize.height - 200, 200); // Batasi tinggi
                scrollPane.setPreferredSize(new Dimension(newWidth, newHeight));
                scrollPane.revalidate();
            }
        });
    }

    private void calculateSAW() {
        try {
            double[][] decisionMatrix = new double[3][3];
            double[] weights = new double[3];
            boolean[] isBenefit = new boolean[3];
            double totalWeight = 0.0;

            // Memasukkan nilai ke dalam decision matrix dan bobot
            for (int i = 0; i < 3; i++) {
                weights[i] = Double.parseDouble(weightFields[i].getText());
                totalWeight += weights[i]; // Menghitung total bobot
                isBenefit[i] = isBenefitCheckboxes[i].isSelected();
                for (int j = 0; j < 3; j++) {
                    decisionMatrix[i][j] = Double.parseDouble(decisionFields[i][j].getText());
                }
            }

            // Validasi apakah total bobot sudah benar (sama dengan 1.0)
            if (totalWeight != 1.0) {
                // Normalisasi bobot jika total bobot tidak sama dengan 1.0
                for (int i = 0; i < weights.length; i++) {
                    weights[i] = weights[i] / totalWeight;
                }
            }

            // Menghitung hasil SAW
            double[] normalizedScores = new double[3];
            double[][] normalizedMatrix = new double[3][3];

            for (int j = 0; j < 3; j++) {
                double max = Double.MIN_VALUE;
                double min = Double.MAX_VALUE;

                for (int i = 0; i < 3; i++) {
                    if (isBenefit[j]) {
                        max = Math.max(max, decisionMatrix[i][j]);
                    } else {
                        min = Math.min(min, decisionMatrix[i][j]);
                    }
                }

                for (int i = 0; i < 3; i++) {
                    if (isBenefit[j]) {
                        normalizedMatrix[i][j] = decisionMatrix[i][j] / max;
                    } else {
                        normalizedMatrix[i][j] = min / decisionMatrix[i][j];
                    }
                }
            }

            // Menghitung skor akhir untuk setiap alternatif
            for (int i = 0; i < 3; i++) {
                double score = 0.0;
                for (int j = 0; j < 3; j++) {
                    score += normalizedMatrix[i][j] * weights[j];
                }
                normalizedScores[i] = score;
            }

            // Menampilkan hasil di text area
            StringBuilder sb = new StringBuilder();
            sb.append("Results:\n");
            for (int i = 0; i < normalizedScores.length; i++) {
                sb.append("Alternative ").append(i + 1).append(": ").append(normalizedScores[i]).append("\n");
            }

            resultArea.setText(sb.toString());

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter valid numbers.");
        }
    }

    private void showHelpPopup() {
        String message = "Aplikasi SAW (Simple Additive Weighting)\n\n"
                + "Aplikasi ini digunakan untuk melakukan penilaian multi-kriteria terhadap beberapa alternatif.\n\n"
                + "Cara Penggunaan Aplikasi:\n"
                + "1. Masukkan nilai untuk setiap alternatif dan kriteria:\n"
                + "   Setiap baris mewakili alternatif (Alt 1, Alt 2, Alt 3), dan setiap kolom mewakili kriteria (K1, K2, K3).\n"
                + "2. Masukkan bobot untuk setiap kriteria:\n"
                + "   Bobot mewakili seberapa penting kriteria tersebut. Jumlah total bobot harus 1.0. Jika jumlah bobot lebih atau kurang dari 1.0, aplikasi akan menormalisasikannya.\n"
                + "3. Tandai kotak 'Benefit?' jika kriteria adalah benefit:\n"
                + "   Jika tidak, biarkan tidak ditandai (cost).\n"
                + "4. Klik tombol 'Calculate SAW':\n"
                + "   Untuk menghitung hasilnya. Hasil akan ditampilkan di area hasil.\n\n"
                + "Tujuan Penggunaan Aplikasi:\n"
                + "Aplikasi ini membantu dalam memilih alternatif terbaik berdasarkan kriteria dan bobot yang diberikan.\n\n"
                + "Contoh Kasus Penggunaan:\n"
                + "Jika Anda membandingkan laptop, masukkan nilai untuk setiap laptop pada kriteria seperti harga dan performa. Aplikasi ini akan menghitung laptop terbaik berdasarkan preferensi Anda.";

        JTextArea helpTextArea = new JTextArea(message);
        helpTextArea.setEditable(false);
        helpTextArea.setLineWrap(true);
        helpTextArea.setWrapStyleWord(true);
        JScrollPane helpScrollPane = new JScrollPane(helpTextArea);
        helpScrollPane.setPreferredSize(new Dimension(400, 300));

        JOptionPane.showMessageDialog(this, helpScrollPane, "Help", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SAWAppUI app = new SAWAppUI();
            app.setVisible(true);
        });
    }
}