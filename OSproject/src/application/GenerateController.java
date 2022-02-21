package application;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.Random;
import java.util.ResourceBundle;

import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

public class GenerateController implements Initializable{

    @FXML
    private TextField file_name;

    @FXML
    private TextField memory_size;

    @FXML
    private TextField min_frames;

    @FXML
    private TextField processes_number;

    @FXML
    private Button generate_button;

    @FXML
    private Button back_button;
    
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		
	}
	
    @FXML
    void generate(ActionEvent event) throws IOException {
    	if(file_name.getText().isEmpty() || memory_size.getText().isEmpty() || min_frames.getText().isEmpty() || processes_number.getText().isEmpty()) {
    		Alert("Check Text Feilds Input");
    		return;
    	}
    	if(Integer.parseInt(min_frames.getText())*Integer.parseInt(processes_number.getText()) > Integer.parseInt(memory_size.getText())) {
    		Alert("Data Error!");
    		min_frames.clear();
    		processes_number.clear();
    		return;
    	}
    	File file = new File(file_name.getText()+".txt");
		file.createNewFile();
		FileWriter out = new FileWriter(file);
		out.write(processes_number.getText());
		out.write("\n");
		out.write(memory_size.getText());
		out.write("\n");
		out.write(min_frames.getText());
		out.write("\n");
		
		int Pid;
		int start;
		int Duration;
		int num = Integer.parseInt(processes_number.getText());
		Random random = new Random();
		String s = "RW";
		int n;
		for(int i=0;i<num;i++) {
			
			Pid= random.nextInt(899)+100;
			Duration= random.nextInt(9)+1;
			start= random.nextInt(4);
			n = random.nextInt(7)+3;
			out.write(""+Pid);
			out.write(" "+start);
			out.write(" "+Duration);
			out.write(" "+n);
			out.write(" ");
			for(int j=0;j<random.nextInt(7)+10;j++) {
				out.write(""+s.charAt(random.nextInt(s.length())));
				out.write(""+random.nextInt(n));
				out.write(" ");
			}
			out.write("\n");
		}
		out.close();
		back(event);
    }

    @FXML
    void back(ActionEvent event) {
        Stage stage = (Stage) back_button.getScene().getWindow();
		FadeTransition ft = new FadeTransition(Duration.millis(500));
		ft.setFromValue(0.0);
		ft.setToValue(1.0);
		ft.play();
        stage.close();
    }
    
	void Alert(String message) {
		javafx.scene.control.Alert alert = new Alert(AlertType.ERROR);
		alert.setContentText(message);
		alert.setTitle("Error!");
		alert.setHeaderText(null);
		alert.setResizable(false);
		alert.initModality(Modality.APPLICATION_MODAL);
		alert.show();
	}

}
