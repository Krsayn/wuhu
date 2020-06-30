import com.matrixone.apps.domain.util.MqlUtil;

public class ExelUtil { 
 //��һ�п�ʼ
 private static int start = 0;
 //���һ�����
 private static int end =0;
 public static String getSubString(String str){
  return str.substring(0,str.lastIndexOf("."));
 }
 /** 
  * ������������Excel�ļ���Sheet������List
  * @param file
  * @param sheetNum
  * @return
  * @throws IOException
  * @author 
  * @date 2013-3-25 ����10:44:26
  * @comment
  */
 public static List<?> exportListFromExcel(File file, String fileFormat,Object dtoobj) 
   throws IOException { 
  return exportListFromExcel(new FileInputStream(file), fileFormat,dtoobj); 
 } 
 /** 
  * ������������Excel����Sheet������List 
  * @param is
  * @param extensionName
  * @param sheetNum
  * @return
  * @throws IOException
  * @author 
  * @date 2013-3-25 ����10:44:03
  * @comment
  */
 public static List<?> exportListFromExcel(InputStream is,String fileFormat,Object dtoobj) throws IOException { 
  Workbook workbook = null; 
  if (fileFormat.equals(BizConstant.XLS)) { 
   workbook = new HSSFWorkbook(is); 
  } else if (fileFormat.equals(BizConstant.XLSX)) { 
   workbook = new XSSFWorkbook(is); 
  } 
  return exportListFromExcel(workbook,dtoobj); 
 } 
 /**
  * ������������ָ����Sheet������List
  * @param workbook
  * @param sheetNum
  * @return
  * @author 
  * @date 2013-3-25 ����10:43:46
  * @comment
  */
 private static List<Object> exportListFromExcel(Workbook workbook ,Object dtoobj) {
	 String chineseID=MqlUtil.mqlCommand(context, "print bus GWSubject Chinese 1 select id dump");
  List<Object> list = new ArrayList<Object>();
  String[] model = null;
  Sheet sheet = workbook.getSheetAt(0); 
  // ������ʽ��� 
  FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator(); 
  int minRowIx = sheet.getFirstRowNum(); 
  int maxRowIx = sheet.getLastRowNum(); 
  for (int rowIx = minRowIx; rowIx <= maxRowIx; rowIx++) { 
   Object obj = null;
   if(rowIx==minRowIx){
    start = sheet.getRow(rowIx).getFirstCellNum();
    end = sheet.getRow(rowIx).getLastCellNum();
   }
   Row row = sheet.getRow(rowIx); 
   StringBuilder sb = new StringBuilder();  
   for (int i = start; i < end; i++) { 
    Cell cell = row.getCell(new Integer(i)); 
    CellValue cellValue = evaluator.evaluate(cell); 
    if (cellValue == null) { 
     sb.append(BizConstant.SEPARATOR+null);
     continue; 
    } 
    // ������ʽ���������ֻ����Boolean��Numeric��String�����������ͣ��������Error�� 
    // �����������ͣ����ݹٷ��ĵ�����ȫ���Ժ��� 
    switch (cellValue.getCellType()) { 
    case Cell.CELL_TYPE_BOOLEAN: 
     sb.append(BizConstant.SEPARATOR + cellValue.getBooleanValue()); 
     break; 
    case Cell.CELL_TYPE_NUMERIC: 
     // ������������ͻᱻת��Ϊ�������ͣ���Ҫ�б�����ִ��� 
     if (DateUtil.isCellDateFormatted(cell)) { 
      sb.append(BizConstant.SEPARATOR + cell.getDateCellValue()); 
     } else { 
      sb.append(BizConstant.SEPARATOR + cellValue.getNumberValue()); 
     } 
     break; 
    case Cell.CELL_TYPE_STRING: 
     sb.append(BizConstant.SEPARATOR + cellValue.getStringValue()); 
     break; 
    case Cell.CELL_TYPE_FORMULA: 
     break; 
    case Cell.CELL_TYPE_BLANK: 
     break; 
    case Cell.CELL_TYPE_ERROR: 
     break; 
    default: 
     break; 
    } 
   } 
   if(rowIx==minRowIx){
    String index = String.valueOf(sb);
    String realmodel =index.substring(1, index.length());
    model =realmodel.split(",");
   }else{
    String index = String.valueOf(sb);
    String realvalue =index.substring(1, index.length());
    String[] value =realvalue.split(",");
    //�ֶ�ӳ��
    try {
     dtoobj =dtoobj.getClass().newInstance();
    } catch (InstantiationException e) {
     e.printStackTrace();
    } catch (IllegalAccessException e) {
     e.printStackTrace();
    }
    obj = reflectUtil(dtoobj,model,value);
    list.add(obj);
   }
  } 
  return list; 
 } 
 /**
  * �����������ֶ�ӳ�丳ֵ
  * @param objOne
  * @param listName
  * @param listVales
  * @return
  * @author 
  * @date 2013-3-25 ����10:53:43
  * @comment
  */
 @SuppressWarnings("deprecation")
 private static Object reflectUtil(Object objOne, String[] listName,
   String[] listVales) {
  Field[] fields = objOne.getClass().getDeclaredFields();
  for (int i = 0; i < fields.length; i++) {
   fields[i].setAccessible(true);
   for (int j = 0; j < listName.length; j++) {
    if (listName[j].equals(fields[i].getName())) {
     try {
      if (fields[i].getType().getName().equals(java.lang.String.class.getName())) { 
       // String type
       if(listVales[j]!=null){
        fields[i].set(objOne, listVales[j]);
       }else{
        fields[i].set(objOne, "");
       }
      } else if (fields[i].getType().getName().equals(java.lang.Integer.class.getName())
        || fields[i].getType().getName().equals("int")) { 
       // Integer type 
       if(listVales[j]!=null){
        fields[i].set(objOne, (int)Double.parseDouble(listVales[j])); 
       }else{
        fields[i].set(objOne, -1); 
       }
      }else if(fields[i].getType().getName().equals("Date")){
       //date type
       if(listVales[j]!=null){
        fields[i].set(objOne, Date.parse(listVales[j]));
       } 
      }else if(fields[i].getType().getName().equals("Double")
        ||fields[i].getType().getName().equals("float")){
       //double
       if(listVales[j]!=null){
        fields[i].set(objOne, Double.parseDouble(listVales[j])); 
       }else{
        fields[i].set(objOne, 0.0); 
       }
      }
     } catch (IllegalArgumentException e) {
      e.printStackTrace();
     } catch (IllegalAccessException e) {
      e.printStackTrace();
     }
     break;
    }
   }
  }
  return objOne;
 }
}