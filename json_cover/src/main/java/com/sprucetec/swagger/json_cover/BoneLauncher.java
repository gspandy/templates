package com.sprucetec.swagger.json_cover;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;


public class BoneLauncher {
	public static void main(String[] args) throws Exception {
		boolean debug=false;
		String pathname=null;
		JSONCover jc=new JSONCover();
		for(String s:args){
			if(s.equals("-d")){
				debug=true;
			}else{
				pathname=s;
			}
		}
		
		if(pathname==null){
			jc.cover(null,debug);
		}else{
			File file=new File(pathname);
			if(file.isDirectory()){
				List<File[]> fileArrList=listAllFile(file);
				for(File[] farr:fileArrList){
					for(File f:farr){
						System.out.println("cover:"+f.getName());
						jc.cover(f,debug);
						System.out.println("done!");
					}
				}
			}else{
				jc.cover(file,debug);
			}
		}
	}
	private static File[] listFile(File file){
		if(file.exists()&&file.isDirectory()){
			return file.listFiles(new FileFilter(){
				public boolean accept(File file) {
					if(file.isFile()&&file.getName().endsWith(".desc")){
						return true;
					}
					return false;
				}});
		}
		return null;
	}
	
	private static File[] listDir(File file){
		if(file.exists()&&file.isDirectory()){
			return file.listFiles(new FileFilter(){
				public boolean accept(File file) {
					if(file.isDirectory()){
						return true;
					}
					return false;
				}});
		}
		return null;
	}
	private static List<File[]> listAllFile(File file){
		List<File[]> list=new ArrayList<File[]>();
		list.add(listFile(file));
		File[] dirs=listDir(file);
		for(File dir:dirs){
			list.add(listFile(dir));
		}
		return list;
	}
	
}