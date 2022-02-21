package application;

public class PageTable {
	private String process_name="NULL";
	private int pid=-1;
	private boolean Valid=true;
	private int frame_num=-1;
	private boolean Dirty=false;
	
	public PageTable() {
		super();
		// TODO Auto-generated constructor stub
	}

	public PageTable(int pid, String process_name, boolean isValid, int frame_num, boolean isDirty) {
		super();
		this.process_name=process_name;
		this.pid = pid;
		this.Valid = isValid;
		this.frame_num = frame_num;
		this.Dirty = isDirty;
	}

	public String getProcess_name() {
		return process_name;
	}

	public void setProcess_name(String process_name) {
		this.process_name = process_name;
	}

	public int getPid() {
		return pid;
	}

	public void setPid(int pid) {
		this.pid = pid;
	}

	public boolean isValid() {
		return Valid;
	}

	public void setValid(boolean valid) {
		Valid = valid;
	}

	public int getFrame_num() {
		return frame_num;
	}

	public void setFrame_num(int frame_num) {
		this.frame_num = frame_num;
	}

	public boolean isDirty() {
		return Dirty;
	}

	public void setDirty(boolean dirty) {
		Dirty = dirty;
	}

	@Override
	public String toString() {
		return "PageTable [process_name=" + process_name + ", pid=" + pid + ", Valid=" + Valid + ", frame_num="
				+ frame_num + ", Dirty=" + Dirty + "]";
	}
	
}
