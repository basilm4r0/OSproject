package application;

import java.util.ArrayList;
import java.util.Arrays;

import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class Process extends MainController implements Runnable {
	private String[] process;
	private int num_of_pages;
	private String process_name;
	private int pid;
	TextArea text;
	TextField total;
	TextField total_hits;
	ProgressBar progress_bar;
	String algorithm;
	String name;
	Thread t;
	int current = 0,pos=0,process_fault=0,process_hit=0;
	String[] frames = new String[min_frames];
	int[] index = new int[min_frames];

	Process(String process_name,int id,String process_line, TextArea text, String algorithm, TextField total, TextField total_hits, ProgressBar progress_bar){
		this.pid = id;
		this.process_name=process_name;
		this.text = text;
		this.algorithm=algorithm;
		this.total = total;
		this.total_hits = total_hits;
		this.progress_bar=progress_bar;
		name = process_name; 
		process_line =process_line.substring(8);
		this.num_of_pages= Integer.parseInt(process_line.substring(0, process_line.indexOf(" "))); 
		process_line = process_line.substring(process_line.indexOf(' ')+1);
		this.process = process_line.split(" ");
		Arrays.fill(frames, "*");
		t = new Thread(this, name);
		text.appendText("\nNew thread: "+t+"\n");
		t.start();
	}

	public void run() {
		try {
			text.appendText(name+" : "+Arrays.toString(process)+"\nNumber of Pages = "+num_of_pages+
					"\nCurrent Allocated Frames = "+Arrays.toString(frames)+"\n");
			System.out.println(name+" : "+Arrays.toString(process));
			System.out.println("Number of Pages = "+num_of_pages);
			System.out.println("Current Allocated Frames = "+Arrays.toString(frames)+"\n");
			MMU(process_name,pid, num_of_pages);
			Thread.sleep(100);
		}catch (InterruptedException e) {
			System.out.println(name + " Interrupted");
			text.appendText("["+name+"] Interrupted.\n");
		}
		text.appendText("["+name+"] Exiting.\nWith Number of Disk Accesses = "+process_fault+"\nAnd Number of Hits = "+process_hit+"\n\n");
		progress += 1/ (double)num_of_processes; 
		progress_bar.setProgress(progress); 
	}

	public void MMU(String process_name, int pid, int num_of_pages) throws InterruptedException {
		if(algorithm.equals("First In First Out")) {
			FIFOPageReplacement();
		}
		else if(algorithm.equals("Least Recenlty Used")){
			LRUPageReplacement();	
		}
		
	}

	public String[] getProcess() {
		return process;
	}

	public int getProcess_fault() {
		return process_fault;
	}
	
	public int getProcess_hit() {
		return process_hit;
	}

	boolean flag=true;
	public void FIFOPageReplacement() throws InterruptedException {
		String mode;
		int page_number;
		for(int i=0;i<memory_size;i++)
			if(memory[i].isValid() & current < min_frames) {
				memory[i].setValid(false);
				index[current]=i;
				current++;
			}
		for(int j=0;j<process.length;j++) {
			mode = process[j].substring(0,1);
			page_number = Integer.parseInt(process[j].substring(1,process[j].length()));
			flag = true;
			for(int i=0;i<min_frames;i++) {
				//if(frames[i].equals(process[j])){
				if(!frames[i].equals("*")) {
					if(Integer.parseInt(frames[i].substring(1,frames[i].length()))==page_number){
						if(frames[i].substring(0,1).equals("R") && mode.equals("W")) {
							frames[i]=process[j];
							memory[index[i]].setDirty(true);
							process_fault++;
							faults++;
						}
						else if(frames[i].substring(0,1).equals("W") && mode.equals("R")){
							frames[i]=process[j];
							memory[index[i]].setDirty(false);
						}
						hits++;
						synchronized (text) {
							Thread.sleep(100);
							text.appendText("["+name +":"+process[j]+"] : Hit | Dirty = "+memory[index[i]].isDirty()+
									" | Current Allocated Frames = "+Arrays.toString(frames)+"\n");
							total_hits.setText(""+hits);
							total.setText(""+faults);
						}
						process_hit++;
						System.out.print("["+name +":"+process[j]+"] ");
						System.out.print ("Hit | Number of faults = "+faults+" | ");
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
				memory[index[pos]].setFrame_num(page_number);
				memory[index[pos]].setPid(pid);
				memory[index[pos]].setProcess_name(process_name);
				if(mode.equalsIgnoreCase("W")) {
					Thread.sleep(300);
					faults++;
					process_fault++;
					memory[index[pos]].setDirty(true);
				}
				else
					memory[index[pos]].setDirty(false);
				faults++;
				process_fault++;
				synchronized (text) {
					Thread.sleep(300);
					text.appendText("["+name +":"+process[j]+"] : Miss | Dirty = "+memory[index[pos]].isDirty()+
							" | Current Allocated Frames = "+Arrays.toString(frames)+"\n");
					total.setText(""+faults);
				}
				pos++;
				System.out.print("["+name +":"+process[j]+"] ");
				System.out.print("Miss | Number of faults = "+faults+" | ");
				System.out.println("Current Allocated Frames = "+Arrays.toString(frames));
			}
			Thread.sleep(1000);
		}
	}

	
	Boolean isFull = false;
	int search = -1;
	ArrayList<String> stack = new ArrayList<String>();
	
	public void LRUPageReplacement() throws InterruptedException {
		for(int i=0;i<memory_size;i++)
			if(memory[i].isValid() & current < min_frames) {
				memory[i].setValid(false);
				index[current]=i;
				current++;
			}	
		String mode;
		int page_number;
		for(int i = 0; i < process.length; i++){
			if(stack.contains(process[i])){
				stack.remove(stack.indexOf(process[i]));
			}
			stack.add(process[i]);
			int search = -1;
			mode = process[i].substring(0,1);
			page_number = Integer.parseInt(process[i].substring(1,process[i].length()));
			for(int j = 0; j < min_frames; j++){
				//if(frames[j].equals(process[i])){
				if(!frames[j].equals("*")) {
					if(Integer.parseInt(frames[j].substring(1,frames[j].length()))==page_number){
						if(frames[j].substring(0,1).equals("R") && mode.equals("W")) {
							frames[j]=process[i];
							memory[index[j]].setDirty(true);
							process_fault++;
							faults++;
						}
						else if(frames[j].substring(0,1).equals("W") && mode.equals("R")){
							frames[j]=process[i];
							memory[index[j]].setDirty(false);
						}
						hits++;
						process_hit++;
						synchronized (text) {
							Thread.sleep(100);
							text.appendText("["+name +":"+process[i]+"] : Hit | Dirty = "+memory[index[j]].isDirty()+
									" | Current Allocated Frames = "+Arrays.toString(frames)+"\n");
							total.setText(""+faults);
							total_hits.setText(""+hits);
						}
						search = j;
						System.out.print("["+name +":"+process[i]+"] ");
						System.out.print ("Hit | Number of faults = "+faults+" | ");
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
				memory[index[pos]].setFrame_num(page_number);
				memory[index[pos]].setPid(pid);
				memory[index[pos]].setProcess_name(process_name);
				if(mode.equalsIgnoreCase("W")) {
					Thread.sleep(500);
					faults++;
					process_fault++;
					memory[index[pos]].setDirty(true);
				}
				else
					memory[index[pos]].setDirty(false);
				
				faults++;
				process_fault++;
				synchronized (text) {
					Thread.sleep(500);
					text.appendText("["+name +":"+process[i]+"] : Miss | Dirty = "+memory[index[pos]].isDirty()+
							" | Current Allocated Frames = "+Arrays.toString(frames)+"\n");
					total.setText(""+faults);
				}
				pos++;
				System.out.print("["+name +":"+process[i]+"] ");
				System.out.print("Miss | Number of faults = "+faults+" | ");
				System.out.println("Current Allocated Frames = "+Arrays.toString(frames));
				if(pos == min_frames){
					pos = 0;
					isFull = true;
				}
			}
			Thread.sleep(1000);
		}
	}

}
