package org.victoria2.tools.vic2sgea.entities;

import java.util.HashMap;
import java.util.Map;

/**
 * Holds the flag and a longer name than just three letters. There will only be
 * one instance of this class for each country as opposed to
 * <code>JoinedCountry</code>, which has many instances.
 */
public class Country extends EconomySubject implements Comparable<Country> {

	protected long population;
	protected long popAristocrats;
	protected long popCapitalists;
	protected long popBureaucrats;
	protected long popArtisans;
	protected long popClergymen;
	protected long popClerks;
	protected long popOfficers;
	protected long popCraftsmen;
	protected long popFarmers;
	protected long popSoldiers;
	protected long popLaboureres;
	protected long popSerfs;
	protected long popSlaves;
	protected float literacy;

	protected long employmentFactory;
	protected long employmentRGO;
	public long workforceFactory;
	public long workforceRGO;

	public GovernmentType government = GovernmentType.Absolute;

	@NonSerializable // too expensive to serialize this
	private Map<String, ProductStorage> storageMap = new HashMap<>();

	private String officialName = "";

	private float GDPPart;
	private int GDPPlace;
	private float goldIncome;
	private String tag;

	public void add(Country that) {
		super.add(that);
		population += that.population;

		goldIncome += that.goldIncome;

		workforceRGO += that.workforceRGO;
		workforceFactory += that.workforceFactory;
		employmentRGO += that.employmentRGO;
		employmentFactory += that.employmentFactory;
	}

	/**
	 * Adds intermediate consumption for given product in that country
	 *
	 * @param product product
	 * @param value   value of consumption to add
	 */
	public void addIntermediate(Product product, float value) {
		ProductStorage storage = findStorage(product);
		storage.incGdp(-value);
	}

	public Country(String tag) {
		super();
		this.tag = tag;
		this.officialName = tag;
	}

	public void calcGdpPart(Country totalCountry) {
		GDPPart = gdp / totalCountry.gdp * 100;
	}

	@Override
	public int compareTo(Country that) {
		return Float.compare(gdp, that.gdp);
	}

	public boolean exist() {
		return population > 0;
	}

	public ProductStorage findStorage(Product product) {
		return storageMap.computeIfAbsent(product.getName(), k -> new ProductStorage(product));
	}
	
	public long getEmployment() {
		return employmentRGO + employmentFactory;
	}

	public long getEmploymentRGO() {
		return employmentRGO;
	}
	
	public long getEmploymentFactory() {
		return employmentFactory;
	}

	public float getGdpPerCapita() {
		return gdp / (float) population;
	}

	public float getGDPPart() {
		return GDPPart;
	}

	public int getGDPPlace() {
		return GDPPlace;
	}

	public long getGoldIncome() {
		return (long) goldIncome;
	}

	public long getPopulation() {
		return population;
	}
	public long getPopulation(String type) {
		if (type.matches("aristocrats"))
			return popAristocrats;
		else if (type.matches("capitalists"))
			return popCapitalists;
		else if (type.matches("bureaucrats"))
			return popBureaucrats;
		else if (type.matches("artisans"))
			return popArtisans;
		else if (type.matches("clergymen"))
			return popClergymen;
		else if (type.matches("clerks"))
			return popClerks;
		else if (type.matches("officers"))
			return popOfficers;
		else if (type.matches("craftsmen"))
			return popCraftsmen;
		else if (type.matches("farmers"))
			return popFarmers;
		else if (type.matches("soldiers"))
			return popSoldiers;
		else if (type.matches("labourers"))
			return popLaboureres;
		else if(type.matches("serfs"))
			return popSerfs;
		else if(type.matches("slaves"))
			return popSlaves;
		else
			return 0;
	}

	public float getPopulationf() {
		return (float) population;
	}
	
	public float getLiteracy() {
		return this.literacy / this.population * 100;
	}

	public float getUnemploymentRate() {
		long totalWorkforce = workforceRGO + workforceFactory;
		return (float) totalWorkforce * 4 / population * 100;
	}

	public float getUnemploymentRateRgo() {
		return (float) ((workforceRGO - employmentRGO) / (float) workforceRGO * 100);
	}

	public float getUnemploymentRateFactory() {
		return (float) (workforceFactory - employmentFactory) / (float) workforceFactory * 100;
	}

	public long getWorkforceRgo() {
		return workforceRGO;
	}
	
	public float getWorkforceRgof() {
		return (float)workforceRGO;
	}

	public long getWorkforceFactory() {
		return workforceFactory;
	}
	
	public float getWorkforceFactoryf() {
		return (float)workforceFactory;
	}

	public String getOfficialName() {
		return officialName;
	}

	public void setOfficialName(String officialName) {
		this.officialName = officialName.trim();
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public String getFlag() {
		if (government == GovernmentType.Absolute || government == GovernmentType.Theocracy
				|| government == GovernmentType.SemiMonarchy || government == GovernmentType.Dictatorship)
			return "";
		else if (government == GovernmentType.Monarchy)
			return "_monarchy";
		else if (government == GovernmentType.Republic)
			return "_republic";
		else if (government == GovernmentType.Communism)
			return "_communist";
		else if (government == GovernmentType.Fascist)
			return "_fascist";
		else
			return "";
	}

	public String getGovernment() {
		if (government == GovernmentType.Absolute)
			return "전제 군주국";
		else if (government == GovernmentType.Theocracy)
			return "신정국가";
		else if (government == GovernmentType.SemiMonarchy)
			return "준 입헌 군주국";
		else if (government == GovernmentType.Monarchy)
			return "입헌 군주국";
		else if (government == GovernmentType.Republic)
			return "공화국";
		else if (government == GovernmentType.Dictatorship)
			return "대통령 독재제";
		else if (government == GovernmentType.Communism)
			return "무산계급 독재제";
		else if (government == GovernmentType.Fascist)
			return "파시스트 독재제";
		else
			return "";
	}

	public void setGovernment(String government) {
		if (government.matches("absoulute_monarchy"))
			this.government = GovernmentType.Absolute;
		else if (government.matches("theocracy"))
			this.government = GovernmentType.Theocracy;
		else if (government.matches("prussian_constitutionalism"))
			this.government = GovernmentType.SemiMonarchy;
		else if (government.matches("hms_government"))
			this.government = GovernmentType.Monarchy;
		else if (government.matches("democracy"))
			this.government = GovernmentType.Republic;
		else if (government.matches("presidential_dictatorship"))
			this.government = GovernmentType.Dictatorship;
		else if (government.matches("proletarian_dictatorship"))
			this.government = GovernmentType.Communism;
		else if (government.matches("fascist_dictatorship"))
			this.government = GovernmentType.Fascist;
	}

	/**
	 * Clears calculated fields so that multiple inner calculations handled
	 * correctly
	 */
	private void clearCalculated() {
		totalSupply = 0;
		sold = 0;
		bought = 0;
		imported = 0;
		exported = 0;

		gdp = 0;

	}

	/**
	 * Calculate inside-country data
	 */
	public void innerCalculations() {
		clearCalculated();
		for (ProductStorage productStorage : getStorage().values()) {

			productStorage.innerCalculations();

			totalSupply += productStorage.getTotalSupplyPounds();
			sold += productStorage.getActualSupplyPounds();
			bought += productStorage.getActualDemandPounds();
			imported += productStorage.getImportedPounds();
			exported += productStorage.getExportedPounds();
			gdp += productStorage.getGdpPounds();

		}

	}

	@Override
	public String toString() {
		return "Country [태그 = " + tag + ", 국명 = " + officialName + "]";
	}

	public Map<String, ProductStorage> getStorage() {
		return storageMap;
	}

	public void setGDPPlace(int GDPPlace) {
		this.GDPPlace = GDPPlace;
	}

	public void addPopulation(int value, String type) {
		population += value;
		if (type.matches("aristocrats"))
			this.popAristocrats += value;
		else if (type.matches("capitalists"))
			this.popCapitalists += value;
		else if (type.matches("bureaucrats"))
			this.popBureaucrats += value;
		else if (type.matches("artisans"))
			this.popArtisans += value;
		else if (type.matches("clergymen"))
			this.popClergymen += value;
		else if (type.matches("clerks"))
			this.popClerks += value;
		else if (type.matches("officers"))
			this.popOfficers += value;
		else if (type.matches("craftsmen"))
			this.popCraftsmen += value;
		else if (type.matches("farmers"))
			this.popFarmers += value;
		else if (type.matches("soldiers"))
			this.popSoldiers += value;
		else if (type.matches("labourers"))
			this.popLaboureres += value;
		else if(type.matches("serfs"))
			this.popSerfs += value;
		else if(type.matches("slaves"))
			this.popSlaves += value;
	}
	
	public void addLiteracy(int popSize, float percent)
	{
		this.literacy += popSize*percent;
	}

	public void addEmploymentRgo(int value) {
		employmentRGO += value;
	}

	public void addEmploymentFactory(int value) {
		employmentFactory += value;
	}

	public void addGoldIncome(float value) {
		goldIncome += value;
	}
}