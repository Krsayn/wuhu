package ext;

import java.util.ArrayList;
import java.util.List;

public class TreeEntity {

	private int id;
	private int pid;
	private Object data;
	private List<TreeEntity> childs=null;
	public TreeEntity(int id,int pid,Object data) {
		this.id=id;
		this.pid=pid;
		this.data=data;
		this.childs=null;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getPid() {
		return pid;
	}
	public void setPid(int pid) {
		this.pid = pid;
	}
	public Object getData() {
		return data;
	}
	public void setData(Object data) {
		this.data = data;
	}
	public List<TreeEntity> getChilds() {
		if(childs==null)
			childs=new ArrayList<TreeEntity>();
		return childs;
	}
	public void setChilds(List<TreeEntity> childs) {
		this.childs = childs;
	}
	
	
}
