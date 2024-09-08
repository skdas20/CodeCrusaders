import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ImageBackgroundGUI extends JFrame {
    private JLabel titleLabel;
    private JTextArea textArea;
    private JButton uploadButton, processButton, saveButton;
    private JFileChooser fileChooser;
    private BufferedImage originalImage;
    private File selectedImageFile;
    private String extractedText = "";

    public ImageBackgroundGUI() {
        setTitle("PII Detection with Image Background");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Load background image
        JLabel backgroundLabel = new JLabel(new ImageIcon("image.png"));
        backgroundLabel.setLayout(new GridBagLayout());
        add(backgroundLabel);

        // UI components
        titleLabel = new JLabel("Upload an Image for PII Detection", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);

        textArea = new JTextArea(10, 40);
        textArea.setOpaque(false); // Make the textarea transparent
        textArea.setForeground(Color.WHITE);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setOpaque(false); // Make the scroll pane transparent
        scrollPane.getViewport().setOpaque(false); // Make the viewport transparent

        uploadButton = new JButton("Upload Image");
        processButton = new JButton("Process for PII");
        saveButton = new JButton("Save Processed Image");
        saveButton.setEnabled(false);

        // Set up layout on top of the background image
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0;
        gbc.gridy = 0;
        backgroundLabel.add(titleLabel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        backgroundLabel.add(scrollPane, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        backgroundLabel.add(uploadButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        backgroundLabel.add(processButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        backgroundLabel.add(saveButton, gbc);

        // Action listeners for buttons
        uploadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fileChooser = new JFileChooser();
                int result = fileChooser.showOpenDialog(null);
                if (result == JFileChooser.APPROVE_OPTION) {
                    selectedImageFile = fileChooser.getSelectedFile();
                    try {
                        originalImage = ImageIO.read(selectedImageFile);
                        textArea.setText("Image uploaded: " + selectedImageFile.getName());
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(null, "Error loading image");
                    }
                }
            }
        });

        processButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (originalImage != null) {
                    // Extract text and detect PII
                    detectAndBlurPII();
                    textArea.setText("<html><b><font size='25'>PII Found</font></b></html>");
                    saveButton.setEnabled(true);  // Enable the save button after processing
                    playAudio("audio.wav");
                } else {
                    JOptionPane.showMessageDialog(null, "Please upload an image first.");
                }
            }
        });

        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser saveChooser = new JFileChooser();
                int result = saveChooser.showSaveDialog(null);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File saveFile = saveChooser.getSelectedFile();
                    try {
                        ImageIO.write(originalImage, "png", saveFile);
                        textArea.setText("Processed image saved successfully!");
                        openImage(saveFile);
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(null, "Error saving image.");
                    }
                }
            }
        });
    }

    // Method to extract text from the image and blur PII areas
    private void detectAndBlurPII() {
        // Assuming we extracted text and coordinates (use OCR like Tesseract)
        String extractedText = "Aadhar: 1234-5678-9012";  // Dummy text for testing

        // Example Aadhar number regex
        Pattern aadharPattern = Pattern.compile("\\d{4}-\\d{4}-\\d{4}");
        Matcher matcher = aadharPattern.matcher(extractedText);

        if (matcher.find()) {
            // Simulate getting bounding box of the detected PII (based on OCR output)
            int x = 150;  // Example x-coordinate of the detected PII
            int y = 100;  // Example y-coordinate of the detected PII
            int width = 300;  // Width of the detected PII
            int height = 70;  // Height of the detected PII

            // Blur the detected PII
            blurArea(x + 20, y - 10, width, height);  // Shift right and up
        }
    }

    // Method to blur specific areas in the image
   // Method to blur specific areas in the image
// Method to blur specific areas in the image
private void blurArea(int x, int y, int width, int height) {
    Graphics2D g2d = originalImage.createGraphics();

    // Apply an opaque black blur effect
    g2d.setColor(Color.BLACK);  // Opaque black with 180 alpha value
    // Adjusted position and size to move it to the bottom-middle
    int blurX = x + 100;  // Shift blur to the right
    int blurY = y + (height / 2) + (originalImage.getHeight() / 4) + 180;  // Move blur downwards
    int blurWidth = width + 150;  // Make the blur area wider
    int blurHeight = height + 20;  // Make the blur area taller

    g2d.fillRect(blurX, blurY, blurWidth, blurHeight);

    g2d.dispose();
}



    // Method to play audio file
    private void playAudio(String audioFilePath) {
        try {
            File audioFile = new File(audioFilePath);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Method to open image using the default image viewer
    private void openImage(File imageFile) {
        try {
            Desktop.getDesktop().open(imageFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ImageBackgroundGUI().setVisible(true));
    }
}
