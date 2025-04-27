import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;

public class QuizApp extends Frame implements ActionListener {
    // Question class
    static class Question {
        String question;
        String[] options;
        int correctIndex;

        public Question(String question, String[] options, int correctIndex) {
            this.question = question;
            this.options = options;
            this.correctIndex = correctIndex;
        }
    }

    ArrayList<Question> questions = new ArrayList<>();
    ArrayList<Integer> userAnswers = new ArrayList<>();

    Label questionLabel;
    CheckboxGroup optionsGroup;
    Checkbox[] optionBoxes = new Checkbox[4];
    Button nextButton;

    int currentIndex = 0;

    public QuizApp() {
        setTitle("Indian GK Quiz App");
        setSize(600, 400);
        setLayout(new GridLayout(6, 1));
        setLocationRelativeTo(null);
        setBackground(new Color(240, 240, 240)); // Light gray background

        // Add 10 questions
        questions.add(new Question("Who was Maharana Pratap's loyal horse?", new String[]{"Chetak", "Bucephalus", "Ashwathama", "Badal"}, 0));
        questions.add(new Question("Which battle did Maharana Pratap fight against the Mughals?", new String[]{"Battle of Panipat", "Battle of Haldighati", "Battle of Plassey", "Battle of Buxar"}, 1));
        questions.add(new Question("Who was Rani Durgavati?", new String[]{"Queen of Mewar", "Queen of Gondwana", "Queen of Bengal", "Queen of Marwar"}, 1));
        questions.add(new Question("Rani Durgavati died fighting against which Mughal general?", new String[]{"Akbar", "Sher Shah", "Asaf Khan", "Aurangzeb"}, 2));
        questions.add(new Question("Where was Guru Nanak Dev Ji born?", new String[]{"Amritsar", "Talwandi", "Delhi", "Patna"}, 1));
        questions.add(new Question("Guru Nanak Dev Ji founded which religion?", new String[]{"Hinduism", "Jainism", "Sikhism", "Islam"}, 2));
        questions.add(new Question("Who was the 10th Sikh Guru?", new String[]{"Guru Teg Bahadur", "Guru Nanak", "Guru Gobind Singh", "Guru Arjan Dev"}, 2));
        questions.add(new Question("Guru Gobind Singh Ji founded the Khalsa in which year?", new String[]{"1699", "1707", "1685", "1710"}, 0));
        questions.add(new Question("Who was the father of Shree Ram?", new String[]{"Dasharatha", "Bharata", "Ravana", "Vishwamitra"}, 0));
        questions.add(new Question("Mata Sita was found by whom in a field?", new String[]{"Janaka", "Dasaratha", "Rama", "Valmiki"}, 0));

        questionLabel = new Label();
        questionLabel.setFont(new Font("Arial", Font.BOLD, 16));
        add(questionLabel);

        optionsGroup = new CheckboxGroup();
        for (int i = 0; i < 4; i++) {
            optionBoxes[i] = new Checkbox("", optionsGroup, false);
            optionBoxes[i].setFont(new Font("Arial", Font.PLAIN, 14));
            add(optionBoxes[i]);
        }

        nextButton = new Button("Next");
        nextButton.setFont(new Font("Arial", Font.BOLD, 14));
        nextButton.setBackground(new Color(0, 122, 204)); // Blue button color
        nextButton.setForeground(Color.WHITE); // White text on button
        nextButton.addActionListener(this);
        add(nextButton);

        loadQuestion(currentIndex);

        setVisible(true);
    }

    void loadQuestion(int index) {
        Question q = questions.get(index);
        questionLabel.setText("Q" + (index + 1) + ": " + q.question);
        // Reset the options (clear previous selection)
        for (int i = 0; i < 4; i++) {
            optionBoxes[i].setLabel(q.options[i]);
            optionBoxes[i].setState(false); // Deselect all answers
        }
    }

    public void actionPerformed(ActionEvent e) {
        int selectedIndex = -1;
        // Check which option is selected
        for (int i = 0; i < 4; i++) {
            if (optionBoxes[i].getState()) {
                selectedIndex = i;
                break;
            }
        }

        userAnswers.add(selectedIndex);
        currentIndex++;

        // If there are more questions, load the next question
        if (currentIndex < questions.size()) {
            loadQuestion(currentIndex);
        } else {
            showResults();
        }
    }

    void showResults() {
        removeAll();
        setLayout(new BorderLayout());

        int correct = 0;
        TextArea resultArea = new TextArea("", 20, 60, TextArea.SCROLLBARS_VERTICAL_ONLY);
        resultArea.setEditable(false);
        resultArea.setFont(new Font("Arial", Font.PLAIN, 14));
        resultArea.setBackground(new Color(250, 250, 250)); // Light background for text area
        resultArea.setForeground(new Color(0, 0, 0)); // Black text

        StringBuilder resultText = new StringBuilder();
        for (int i = 0; i < questions.size(); i++) {
            Question q = questions.get(i);
            int userAns = userAnswers.get(i);

            resultText.append("Q").append(i + 1).append(": ").append(q.question).append("\n");
            resultText.append("Your Answer: ");
            resultText.append(userAns != -1 ? q.options[userAns] : "No Answer").append("\n");
            resultText.append("Correct Answer: ").append(q.options[q.correctIndex]).append("\n");

            if (userAns == q.correctIndex) {
                resultText.append("Result: Correct ✅\n\n");
                correct++;
            } else {
                resultText.append("Result: Incorrect ❌\n\n");
            }
        }

        resultText.append("You got ").append(correct).append(" out of ").append(questions.size()).append(" correct.");

        resultArea.setText(resultText.toString());
        add(new Label("Quiz Results", Label.CENTER), BorderLayout.NORTH);
        add(resultArea, BorderLayout.CENTER);

        // Save results to file
        saveResultsToFile(resultText.toString());

        // Add OK button to open the file
        Button okButton = new Button("OK");
        okButton.setFont(new Font("Arial", Font.BOLD, 14));
        okButton.setBackground(new Color(0, 122, 204)); // Blue button color
        okButton.setForeground(Color.WHITE); // White text
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openResultsFile();
            }
        });
        add(okButton, BorderLayout.SOUTH);

        validate();
        repaint();
    }

    void saveResultsToFile(String resultText) {
        try (FileOutputStream fos = new FileOutputStream("quiz_results.txt")) {
            byte[] resultBytes = resultText.getBytes();
            fos.write(resultBytes);
            System.out.println("Results saved to quiz_results.txt");
        } catch (IOException e) {
            System.out.println("Error saving results to file: " + e.getMessage());
        }
    }

    void openResultsFile() {
        try {
            File file = new File("quiz_results.txt");
            if (file.exists()) {
                Desktop.getDesktop().open(file);
            } else {
                System.out.println("File not found");
            }
        } catch (IOException e) {
            System.out.println("Error opening results file: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        new QuizApp();
    }
}
