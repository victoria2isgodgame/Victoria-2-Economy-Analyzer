package org.victoria2.tools.vic2sgea.entities;
import org.victoria2.tools.vic2sgea.main.Report;

import java.util.*;

public class History {
	private String name;
	
	
	private HashMap<String, Report> reports = new HashMap<> ();
	
	
	
	
	public History()
	{
		
	}
	
	public History(String name)
	{
		this.name = name;
	}
	
	
	public History(String reportname, Report report)
	{
		reports.put(reportname, report);
		this.name = report.getPlayerCountry().getOfficialName();
	}
	
	
	public void addReport(String name, Report report)
	{
		reports.put(name,  report);
	}
	
	public Report getReport(String name)
	{
		return reports.get(name);
	}
	
	public HashMap<String, Report> getReportAll()
	{
		return reports;
	}
	
	public String getName()
	{
		return name;
	}
	
	public void setName(String name)
	{
		this.name = name;
	}
	
	public List<Float> getGdp()
	{
		List<Float> list = new ArrayList<>();
		
		for(Report report:reports.values())
		{
			list.add(report.getCountry("TOT").getGdp());
		}
		
		return list;
	}
}
