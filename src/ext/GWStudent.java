package ext;

public class GWStudent {

	private String name;
	private String type;
	private String GWName;
	private String GWSex;
	private String GWClass;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getGWName() {
		return GWName;
	}

	public void setGWName(String gWName) {
		GWName = gWName;
	}

	public String getGWSex() {
		return GWSex;
	}

	public void setGWSex(String gWSex) {
		GWSex = gWSex;
	}

	public String getGWClass() {
		return GWClass;
	}

	public void setGWClass(String gWClass) {
		GWClass = gWClass;
	}

	@Override
	public String toString() {
		return "GWStudent [name=" + name + ", type=" + type + ", GWName=" + GWName + ", GWSex=" + GWSex + ", GWClass="
				+ GWClass + "]";
	}

}
