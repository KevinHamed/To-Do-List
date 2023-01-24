package com.example.todolistgui;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {

        // create root
        VBox root = new VBox();

        // create taskList
        ListView<HBox> taskList = new ListView<>();

        // create input field for each task
        TextField textField = new TextField();
        textField.setPromptText("Enter new task");

        // create input field for due date
        DatePicker dueDatePicker = new DatePicker();
        dueDatePicker.setPromptText("Due Date");

        // create priority box to mark the priority of each task
        ComboBox<String> priorityComboBox = new ComboBox<>();
        priorityComboBox.setItems(FXCollections.observableArrayList("High", "Medium", "Low"));
        priorityComboBox.setPromptText("Priority");

        HBox filterContainer = new HBox();
        ComboBox<String> filterComboBox = new ComboBox<>();
        filterComboBox.setItems(FXCollections.observableArrayList("All", "High Priority", "Medium Priority", "Low Priority", 
        "Completed"));
        filterComboBox.setPromptText("Filter By");

        // Button to add
        Button addButton = new Button("Add Task");
        // action handler for addbutton
        addButton.setOnAction(actionEvent -> {
            LocalDate dueDate = dueDatePicker.getValue();
            String priority = priorityComboBox.getValue();
            String task = textField.getText();
            if (task.isEmpty() || dueDate == null || priority == null) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Missing Input");
                alert.setContentText("Please enter a task, select a due date, and select a priority before adding a task.");
                alert.showAndWait();
            } else {
                String taskString = task + " - Due: " + dueDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")) + " - Priority: " + priority;
                CheckBox checkBox = new CheckBox();
                checkBox.setSelected(false);
                checkBox.setText(taskString);
                HBox hBox = new HBox();
                hBox.getChildren().addAll(checkBox);
                taskList.getItems().add(hBox);
                textField.clear();
                dueDatePicker.setValue(null);
                priorityComboBox.setPromptText("Priority");
            }
        });

        // Button to remove
        Button removeButton = new Button("Remove Task");
        removeButton.setOnAction(actionEvent -> {
            int selectedIndex = taskList.getSelectionModel().getSelectedIndex();
            taskList.getItems().remove(selectedIndex);
        });

        // Button to edit
        Button editButton = new Button("Edit Task");
        editButton.setOnAction(actionEvent -> {

            HBox selectedHBox = taskList.getSelectionModel().getSelectedItem();
            CheckBox selectedCheckBox = (CheckBox) selectedHBox.getChildren().get(0);

            String selectedTask = selectedCheckBox.getText();
            String selectedPriority = "";
            if(selectedTask.contains("High")) {
                selectedPriority = "High";
            }
            else if(selectedTask.contains("Medium")) {
                selectedPriority = "Medium";
            }
            else if(selectedTask.contains("Low")) {
                selectedPriority = "Low";
            }
            int selectedIndex = taskList.getSelectionModel().getSelectedIndex();
            taskList.getItems().remove(selectedHBox);
            
            TextField editField = new TextField();
            editField.setPromptText("Edit Task");

            ComboBox<String> editedPriorityComboBox = new ComboBox<>();
            editedPriorityComboBox.setItems(FXCollections.observableArrayList("High", "Medium", "Low"));
            editedPriorityComboBox.setPromptText("Priority");

            editedPriorityComboBox.setValue(selectedPriority);

            DatePicker editDueDatePicker = new DatePicker();
            editDueDatePicker.setPromptText("Due Date");

            Button saveButton = new Button("Save");

            HBox editBox = new HBox();
            editBox.getChildren().addAll(editField, editDueDatePicker, editedPriorityComboBox, saveButton);
            editBox.setSpacing(10);
            root.getChildren().addAll(editBox);
            
            saveButton.setOnAction(actionEvent1 -> {
                LocalDate editedDueDate = editDueDatePicker.getValue();
                String editedPriority = editedPriorityComboBox.getValue();
                String editedTask = editField.getText() + " - Due: " + editedDueDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")) + " - Priority: " + editedPriority;
                CheckBox editedCheckBox = new CheckBox();
                editedCheckBox.setSelected(false);
                editedCheckBox.setText(editedTask);
                HBox editedHBox = new HBox();
                editedHBox.getChildren().addAll(editedCheckBox);
                taskList.getItems().add(selectedIndex, editedHBox);
                root.getChildren().removeAll(editBox);
                priorityComboBox.setValue(null);
                priorityComboBox.setPromptText("Priority");
            });
        });

        Button sortButton = new Button("Sort by Priority");
        sortButton.setOnAction(actionEvent -> {
            ObservableList<HBox> items = taskList.getItems();
            items.sort((o1, o2) -> {
                CheckBox cb1 = (CheckBox) o1.getChildren().get(0);
                CheckBox cb2 = (CheckBox) o2.getChildren().get(0);
                String t1 = cb1.getText();
                String t2 = cb2.getText();
                int priority1 = 0;
                int priority2 = 0;
                if(t1.contains("High")) priority1 = 1;
                else if(t1.contains("Medium")) priority1 = 2;
                else if(t1.contains("Low")) priority1 = 3;
                else if(t1.contains("null")) priority1 = 4;
                if(t2.contains("High")) priority2 = 1;
                else if(t2.contains("Medium")) priority2 = 2;
                else if(t2.contains("Low")) priority2 = 3;
                else if(t2.contains("null")) priority2 = 4;
                return Integer.compare(priority1, priority2);
            });
        });

        Button clearButton = new Button("Clear completed tasks");
        clearButton.setOnAction(actionEvent -> {
            ObservableList<HBox> items = taskList.getItems();
            for (int i = items.size() - 1; i >= 0; i--) {
                HBox hBox = items.get(i);
                CheckBox checkBox = (CheckBox) hBox.getChildren().get(0);
                if (checkBox.isSelected()) {
                    items.remove(i);
                }
            }
        });

        filterComboBox.setOnAction(event -> {
            String selectedFilter = filterComboBox.getValue();
            if (selectedFilter != null) {
                ObservableList<HBox> items = taskList.getItems();
                for (int i = items.size() - 1; i >= 0; i--) {
                    HBox hBox = items.get(i);
                    CheckBox checkBox = (CheckBox) hBox.getChildren().get(0);
                    String task = checkBox.getText();
                    String priority = task.substring(task.indexOf("Priority:")+10);
                    boolean isCompleted = checkBox.isSelected();
                    if (selectedFilter.equals("All")) {
                        checkBox.setVisible(true);
                    } else if (selectedFilter.equals("High Priority") && priority.equals("High")) {
                        checkBox.setVisible(true);
                    } else if (selectedFilter.equals("Medium Priority") && priority.equals("Medium")) {
                        checkBox.setVisible(true);
                    } else if (selectedFilter.equals("Low Priority") && priority.equals("Low")) {
                        checkBox.setVisible(true);
                    } else if (selectedFilter.equals("Completed") && isCompleted) {
                        checkBox.setVisible(true);
                    } else {
                        checkBox.setVisible(false);
                    }
                }
            }
        });

        Button sortDueDateButton = new Button("Sort by Due Date");
        sortDueDateButton.setOnAction(actionEvent -> {
            ObservableList<HBox> taskListItems = taskList.getItems();
            Collections.sort(taskListItems, (task1, task2) -> {
                CheckBox task1CheckBox = (CheckBox) task1.getChildren().get(0);
                String task1DueDateString = task1CheckBox.getText().split("Due: ")[1].split(" -")[0];
                LocalDate task1DueDate = LocalDate.parse(task1DueDateString, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
                
                CheckBox task2CheckBox = (CheckBox) task2.getChildren().get(0);
                String task2DueDateString = task2CheckBox.getText().split("Due: ")[1].split(" -")[0];
                LocalDate task2DueDate = LocalDate.parse(task2DueDateString, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
                
                return task1DueDate.compareTo(task2DueDate);
            });
            taskList.setItems(taskListItems);
        });
        

        HBox inputBox = new HBox();
        inputBox.setSpacing(10);
        inputBox.getChildren().addAll(textField, priorityComboBox);
        HBox buttonBox = new HBox();
        buttonBox.setSpacing(10);
        buttonBox.getChildren().addAll(addButton, removeButton, sortButton, sortDueDateButton, editButton, clearButton, filterContainer);
        filterContainer.getChildren().addAll(filterComboBox);

        root.setPadding(new Insets(10));
        root.setSpacing(10);
        root.getChildren().addAll(inputBox, dueDatePicker, taskList, buttonBox);

        Scene scene = new Scene(root, 520,550);
        stage.setScene(scene);
        stage.setTitle("To-Do List");
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}