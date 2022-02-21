package application;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.Scanner;

import javafx.animation.FadeTransition;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Duration;

import java.io.IOException;

public class MainController implements Initializable{

	@FXML
	private AnchorPane anchorPane;

	@FXML
	private TreeTableView<ProcessInfo> table;

	@FXML
	private TreeTableColumn<ProcessInfo, String> process_name;

	@FXML
	private TreeTableColumn<ProcessInfo, Number> process_id;

	@FXML
	private TextArea text;

	@FXML
	private Button start_button;

	@FXML
	private Button clear_button;

	@FXML
	private Button run_button;

	@FXML
	private Button exit_button;

	@FXML
	private Button restart_button;

	@FXML
	private Button memory_button;

	@FXML
	private Button process_details;

	@FXML
	private Button generate_button;

	@FXML
	private Button browse_button;

	@FXML
	private CheckBox fifo;

	@FXML
	private CheckBox lru;

	@FXML
	private TextField total;
	
	@FXML
	private TextField total_hits;

	@FXML
	private ProgressBar progress_bar;

	@FXML
	private ComboBox<String> algorithm = new ComboBox<>();

	ObservableList<String> combobox = FXCollections.observableArrayList("First In First Out", "Least Recenlty Used");
	ArrayList<ProcessInfo> list;
	ArrayList<Process> process_list;

	public static PageTable[] memory;
	public static int Pid;
	public static int start;
	public static int Dur;
	public static int memory_size;
	public static int min_frames;
	public static int num_of_processes;
	public static int faults;
	public static int hits;
	public static double progress;
	public static String file_path = "C:\\Users\\islam\\eclipse-workspace\\OSproject\\Input.txt";
	Scanner in = null;
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		faults=0;
		hits=0;
		progress=0;
		total.setText("0");
		total_hits.setText("0");
		run_button.setDisable(true);
		
		process_details.setDisable(true);
		progress_bar.setStyle("-fx-accent: #2CFF00;");
		progress_bar.setProgress(0);
		algorithm.setItems(combobox);
		list = new ArrayList<>();
		process_list = new ArrayList<Process>();
		text.setScrollTop(Double.MAX_VALUE);
		File input = new File(file_path);
		try {
			in = new Scanner(input);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		text.setText("Input File Path: "+input.getAbsolutePath()+"\n");
		num_of_processes = in.nextInt();
		memory_size = in.nextInt();
		min_frames = in.nextInt();
		System.out.println(num_of_processes);
		in.nextLine();
		memory = new PageTable[memory_size];
		for(int i=0;i<memory_size;i++) {
			memory[i] = new PageTable();
		}
		text.appendText("Number of Processes = " + num_of_processes +"\nMemory Size = "+ memory_size + "\nMax Frames per Process = " + min_frames +
				 "\n");
	}

	@FXML
	void Start(ActionEvent event) {
		if( algorithm.getSelectionModel().isEmpty()) {
			Alert("Please Select Algorithm");
			return;
		}
		start_button.setDisable(true);
		run_button.setDisable(false);
		
		process_details.setDisable(false);
		String[] process_line =  new String[num_of_processes];
		int i=0;
		String name;
		String[] process;
		TreeItem<ProcessInfo> t = new TreeItem<>();
		t.setExpanded(true);
		text.appendText("\nRunning "+algorithm.getValue()+" Algorithm:\n");
		while(in.hasNextLine()) {
			if(i<process_line.length) {
				process_line[i] = in.nextLine().trim();
				name="process"+i;
				
				Pid=Integer.parseInt(process_line[i].substring(0, 3));
				String x= process_line[i].substring(4, 7);
				start= Integer.parseInt(x.substring(0, x.indexOf(" "))); 
				
				process_list.add(new Process(name, Pid, process_line[i],text,algorithm.getValue(),total,total_hits,progress_bar));
				process_line[i] = process_line[i].substring(10);
				process = process_line[i].split(" ");
				list.add(new ProcessInfo(Pid, name));
				TreeItem<ProcessInfo> root = new TreeItem<>(new ProcessInfo(i,name));
				TreeItem<ProcessInfo> item;
				for(int j=0;j<process.length;j++) {
					item = new TreeItem<>(new ProcessInfo(Pid,process[j]));
					root.getChildren().add(item);
					process_name.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<ProcessInfo, String>,ObservableValue<String>>(){
						@Override
						public ObservableValue<String> call (TreeTableColumn.CellDataFeatures<ProcessInfo, String> param) {
							return param.getValue().getValue().getPname();
						}
					});
					process_id.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<ProcessInfo, Number>,ObservableValue<Number>>(){
						@Override
						public ObservableValue<Number> call (TreeTableColumn.CellDataFeatures<ProcessInfo, Number> param) {
							return param.getValue().getValue().getPid();
						}
					});
				}
				t.getChildren().add(root);
				table.setShowRoot(false);
				table.setRoot(t);
				i++;
			}
		}
		in.close();
	}

	@FXML
	void clear(ActionEvent event) {
		text.clear();
	}

	@FXML
	void run(ActionEvent event) throws InterruptedException {
		TreeItem<ProcessInfo> process = table.getSelectionModel().getSelectedItem();
		if(process == null) {
			Alert("Please Select a Process From the Table");
			return;
		}
		else if(fifo.isSelected()==false && lru.isSelected()==false) {
			Alert("Please check at least one algorithm");
			return;
		}
		String pname = process.getValue().getPname().get();
		int pid = process.getValue().getPid().intValue();
		Process p = process_list.get(pid);
		text.setText("Running on ["+pname+"]: \n\n");
		int[] n1= {0,0},n2= {0,0};
		if(fifo.isSelected()) {
			text.appendText("Running FIFO Algorithm: \n");
			n1 = FIFOPageReplacement(pid, pname,p.getProcess());
			text.appendText("\n");
		}
		if(lru.isSelected()) {
			text.appendText("Running LRU Algorithm: \n");
			n2 = LRUPageReplacement(pid, pname, p.getProcess());
			text.appendText("\n");
		}
	
		if(fifo.isSelected()) {
			text.appendText("FIFO Disk Acesses: "+n1[1]+" \nFIFO Hits: "+n1[0]+"\n");
		}
		if(lru.isSelected()) {
			text.appendText("LRU Disk Acesses: "+n2[1]+" \nLRU Hits: "+n2[0]+"\n");
		}
		
	}

   

	@FXML
	void exit(ActionEvent event) {
		System.exit(0);
	}

	@FXML
	void generate(ActionEvent event) throws IOException {
		AnchorPane show = (AnchorPane)FXMLLoader.load(getClass().getResource("GenerateFile.fxml"));
		FadeTransition ft = new FadeTransition(Duration.millis(200), show);
		ft.setFromValue(0.0);
		ft.setToValue(1.0);
		ft.play();
		Stage stage = new Stage();
		stage.setTitle("Generate File");
		stage.setScene(new Scene(show, 409, 191));
		stage.show();
	}

	@FXML
	void browse(ActionEvent event) throws IOException {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Select File");
		File defaultDirectory = new File("C:\\Users\\islam\\eclipse-workspace\\OSproject");
		fileChooser.setInitialDirectory(defaultDirectory);
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
		fileChooser.getExtensionFilters().add(extFilter);
		Stage stage = (Stage)anchorPane.getScene().getWindow();
		File file = fileChooser.showOpenDialog(stage);
		if(file != null && file.isFile()) {
			file_path = file.getAbsoluteFile().toString();
			
			try {
				in = new Scanner(file);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			text.setText("Input File Path: "+file.getAbsolutePath()+"\n");
			num_of_processes = in.nextInt();
			memory_size = in.nextInt();
			min_frames = in.nextInt();
			in.nextLine();
			memory = new PageTable[memory_size];
			for(int i=0;i<memory_size;i++) {
				memory[i] = new PageTable();
			}
			text.appendText("Number of Processes = " + num_of_processes +"\nMemory Size = "+ memory_size + "\nMax Frames per Process = " + min_frames +
					 "\n");
		}
	}

	@FXML
	void restart(ActionEvent event) throws IOException {
		//Stage stage = (Stage) restart_button.getScene().getWindow();
		//stage.close();
		AnchorPane show = (AnchorPane)FXMLLoader.load(getClass().getResource("MainController.fxml"));
		FadeTransition ft = new FadeTransition(Duration.millis(500), show);
		ft.setFromValue(0.0);
		ft.setToValue(1.0);
		ft.play();
		Scene scene = new Scene(show);
		scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
		window.setScene(scene);
		window.show();
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

	@FXML
	void show_memory(ActionEvent event) throws IOException {
		try {
			AnchorPane show = (AnchorPane)FXMLLoader.load(getClass().getResource("Memory.fxml"));
			FadeTransition ft = new FadeTransition(Duration.millis(500), show);
			ft.setFromValue(0.0);
			ft.setToValue(1.0);
			ft.play();
			Stage stage = new Stage();
			stage.setTitle("Current Memory Allocation");
			stage.setScene(new Scene(show, 780, 566));
			stage.show();
			//((Node)(event.getSource())).getScene().getWindow().hide();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	@FXML
	void show_details(ActionEvent event) {
		TreeItem<ProcessInfo> selected = table.getSelectionModel().getSelectedItem();
		if(selected == null) {
			Alert("Please Select a Process From the Table");
			return;
		}
		text.setText("Show Process Details:\n\n");
		String pname = selected.getValue().getPname().get();
		int pid = selected.getValue().getPid().intValue();
		Process p = process_list.get(pid);
		double ratio = (double)(p.getProcess_fault())/ faults;
		text.appendText("Process Name: "+pname+"\nProcess ID: "+pid+"\nNumber of Disk Accesses: "+p.getProcess_fault()+
				"\nNumber of Hits: "+p.getProcess_hit()+
				"\nMemory Access Reference List: "+Arrays.toString(p.getProcess())+
				"\nProcess's Memory Accesses/Total Memory Accesses: "+ratio+" \n");
	}

	public int[] FIFOPageReplacement(int pid, String name, String[] process) {
		String mode;
		int page_number;
		boolean flag=true,dirty=false;
		String[] frames = new String[min_frames];
		Arrays.fill(frames, "*");
		int fault=0,hit=0,pos=0;
		for(int j=0;j<process.length;j++) {
			mode = process[j].substring(0,1);
			page_number = Integer.parseInt(process[j].substring(1,process[j].length()));
			flag = true;
			for(int i=0;i<min_frames;i++) {
				//if(frames[i].equals(process[j])) {
				if(!frames[i].equals("*")) {
					if(Integer.parseInt(frames[i].substring(1,frames[i].length()))==page_number){
						if(frames[i].substring(0,1).equals("R") && mode.equals("W")) {
							frames[i]=process[j];
							dirty=true;
							fault++;
						}
						else if(frames[i].substring(0,1).equals("W") && mode.equals("R")){
							frames[i]=process[j];
							dirty=false;
						}
						hit++;
						text.appendText("["+name +":"+process[j]+"] Hit | Dirty = "+dirty+" | Current Allocated Frames = "+Arrays.toString(frames)+"\n");
						System.out.print("["+name +":"+process[j]+"] ");
						System.out.print ("Hit | Number of faults = "+fault+" | ");
						System.out.println("Current Allocated Frames = "+Arrays.toString(frames));
						flag = false;
						break;
					}
				}
			}

			if(pos == min_frames)
				pos = 0;
			if(flag) {
				frames[pos]=process[j];
				if(mode.equals("W")) {
					fault++;
					dirty=false;
				}
				else
					dirty=true;
				pos++;
				fault++;
				text.appendText("["+name +":"+process[j]+"] Miss | Dirty = "+dirty+" | Current Allocated Frames = "+Arrays.toString(frames)+"\n");
				System.out.print("["+name +":"+process[j]+"] ");
				System.out.print("Miss | Number of faults = "+fault+" | ");
				System.out.println("Current Allocated Frames = "+Arrays.toString(frames));
			}
		}
		int[] n = new int[2];
		n[0]=hit;
		n[1]=fault;
		return n;
	}

	public int[] LRUPageReplacement(int pid, String name, String[] process) {
		String mode;
		int page_number;
		Boolean isFull = false,dirty=false;
		ArrayList<String> stack = new ArrayList<String>();
		String[] frames = new String[min_frames];
		Arrays.fill(frames, "*");
		int fault=0,hit=0,pos=0;
		for(int i = 0; i < process.length; i++){
			mode = process[i].substring(0,1);
			page_number = Integer.parseInt(process[i].substring(1,process[i].length()));
			if(stack.contains(process[i])){
				stack.remove(stack.indexOf(process[i]));
			}
			stack.add(process[i]);
			int search = -1;
			for(int j = 0; j < min_frames; j++){
				//if(frames[j].equals(process[i])){
				if(!frames[j].equals("*")) {
					if(Integer.parseInt(frames[j].substring(1,frames[j].length()))==page_number){
						if(frames[j].substring(0,1).equals("R") && mode.equals("W")) {
							frames[j]=process[i];
							fault++;
							dirty=true;
						}
						else if(frames[j].substring(0,1).equals("W") && mode.equals("R")){
							frames[j]=process[i];
							dirty=false;
						}
						hit++;
						text.appendText("["+name +":"+process[i]+"] Hit | Dirty = "+dirty+" | Current Allocated Frames = "+Arrays.toString(frames)+"\n");
						search = j;
						System.out.print("["+name +":"+process[i]+"] ");
						System.out.print ("Hit | Number of faults = "+fault+" | ");
						System.out.println("Current Allocated Frames = "+Arrays.toString(frames));
						break;
					}
				}
			}
			if(search == -1){
				if(isFull) {
					int min_loc = process.length-1;
					for(int j = 0; j < min_frames; j++){  
						if(stack.contains(frames[j])){
							int temp = stack.indexOf(frames[j]);
							if(temp < min_loc){
								min_loc = temp;
								pos = j;
							}
						}
					}
				}
				frames[pos] = process[i];
				if(mode.equalsIgnoreCase("W")) {
					fault++;
					dirty=true;
				}
				else
					dirty=false;
				pos++;
				fault++;
				text.appendText("["+name +":"+process[i]+"] Miss | Dirty = "+dirty+" | Current Allocated Frames = "+Arrays.toString(frames)+"\n");
				System.out.print("["+name +":"+process[i]+"] ");
				System.out.print("Miss | Number of faults = "+fault+" | ");
				System.out.println("Current Allocated Frames = "+Arrays.toString(frames));
				if(pos == min_frames){
					pos = 0;
					isFull = true;
				}
			}
		}
		int[] n = new int[2];
		n[0]=hit;
		n[1]=fault;
		return n;
	}

	

	public class ProcessInfo {
		private SimpleIntegerProperty pid;
		private SimpleStringProperty pname;

		public ProcessInfo(int pid, String pname) {
			this.pid = new SimpleIntegerProperty(pid);
			this.pname = new SimpleStringProperty(pname);
		}

		public SimpleIntegerProperty getPid() {
			return pid;
		}

		public SimpleStringProperty getPname() {
			return pname;
		}	
	}
}


