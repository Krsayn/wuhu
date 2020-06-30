package project;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import matrix.db.Context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.matrixone.apps.common.Route;
import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.common.Document;
import com.matrixone.apps.domain.util.PersonUtil;
import com.matrixone.servlet.Framework;

@Path("/CheckDocInEnovia")
public class CheckDocInEnovia {
    
	 final static Logger logger = LoggerFactory.getLogger(CheckDocInEnovia.class);
	 SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	   /**
	     * @Description 测试例子 访问路径类似于：https://r2019x.glaway.com/3dspace/resources/dsic/CheckDocInEnovia/checkIn?ProductId=58813.57799.5487.28411&FilePath=C:\temp\allJPO.exp
	     * @Author jiangzhi
	     * @param request
	     * @return javax.ws.rs.core.Response
	     **/
	    @GET
	    @Path("/checkIn")
	    public Response checkIn(@javax.ws.rs.core.Context HttpServletRequest request)
	    {
	    	String result = "";
		    Context context = null;
		    try{
		    	
		    	logger.info("this CheckDocInEnovia  checkIn request date is "+sdf.format(new Date()));
	    		 try{
	    			 context = Framework.getContext(request.getSession());
	    			 String personId = PersonUtil.getPersonObjectID(context);
	    		 }catch(Exception e){
	    			 logger.error(" this CheckDocInEnovia checkIn "+LoggerWords.LOGGING_HAS_NO_CONTEXT);
		    		 return Response.ok(LoggerWords.HAS_NO_CONTEXT).build();	
	    		 }
	    		 //  "C:\\temp\\allJPO.exp"
		    	 //String filePath = request.getParameter("FilePath");
		    	 //System.out.println("filePath===>"+filePath);
				 
				 //filePath = filePath.replaceAll("\\", "\\\\");
				 String filePath = "C:\\temp\\allJPO.exp";
				 
				 String productId = request.getParameter("ProductId");
				 if(!new DomainObject(productId).exists(context))
	    		  {
	    			  logger.info("this CheckDocInEnovia  checkIn request has no such productId in dataBase,please check in Enovia");
		    		  return Response.ok("has no such productId").build();	
	    		  }
				 File fileInCATIA = new File(filePath);
				 if(!fileInCATIA.exists())
				 {
					 logger.error(" this CheckDocInEnovia checkIn  has no such file in this path :"+filePath);
		    		 return Response.ok(LoggerWords.HAS_NO_File).build();
				 }
				 // 逻辑上是将文件夹结构中同名的文Document 链接到 产品  再在其中checkin  
				 // 目前 先做 创建Docuement 和 checkin
				 Document document = (Document) DomainObject.newInstance(context,DomainConstants.TYPE_DOCUMENT);
				 // 第一个 "" 是 描述  第二个 "" 是 title
		         document = document.create(context, "Document", System.currentTimeMillis()+"", "Document Release", "", "","Chinese" );	
		         String docId = document.getObjectId();
		         String fileName = filePath.split("\\\\")[filePath.split("\\\\").length-1];
		         String originator = PersonUtil.getPersonObject(context).getName();
		         DocumentUtil.checkinDocument(context,docId , filePath, fileName, originator);
		         
		         //将 任务 和 文档 用交付物 关系 关联 
		         
		         // 将 
		         //System.out.println("document==========>"+document.getObjectId());
		    }catch(Exception e){
		    	e.printStackTrace();
		    	System.out.println("CheckDocInEnovia==checkIn=======>"+e.getMessage());
		        logger.error("CheckDocInEnovia check in error:"+e.getMessage());
		        return Response.ok("error").build();	
		    }
		
		 return Response.ok("success").build();	
	    }
	    public static void main(String[] args) {
			String filePath = "C:\\t\\aa.exp";
			System.out.println(filePath.split("\\\\")[filePath.split("\\\\").length-1]);
		}
}
