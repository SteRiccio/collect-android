package org.openforis.collect.android.management;
/**
 * @author S. Ricci
 * @author M. Togna
 * @author E. Wibowo
 * @refactored_by A.Voronov
 */
import java.util.ArrayList;
import java.util.List;

import org.openforis.collect.persistence.TaxonDao;
import org.openforis.collect.persistence.TaxonVernacularNameDao;
import org.openforis.collect.persistence.TaxonomyDao;
import org.openforis.idm.model.TaxonOccurrence;
import org.openforis.idm.model.species.Taxon;
import org.openforis.idm.model.species.TaxonVernacularName;
import org.openforis.idm.model.species.Taxonomy;
import org.springframework.transaction.annotation.Transactional;

public class TaxonManager {

	private TaxonDao taxonDao;
	private TaxonVernacularNameDao taxonVernacularNameDao;
	private TaxonomyDao taxonomyDao;

	public TaxonomyDao getTaxonomyDao(){
		return this.taxonomyDao;
	}
	
	public void setTaxonomyDao(TaxonomyDao taxonomyDao){
		this.taxonomyDao = taxonomyDao;
	}

	public TaxonDao getTaxonDao(){
		return this.taxonDao;
	}
	
	public void setTaxonDao(TaxonDao taxonDao){
		this.taxonDao = taxonDao;
	}	

	public TaxonVernacularNameDao getTaxonVernacularNameDao(){
		return this.taxonVernacularNameDao;
	}
	
	public void setTaxonVernacularNameDao(TaxonVernacularNameDao taxonVernacularNameDao){
		this.taxonVernacularNameDao = taxonVernacularNameDao;
	}	
	
	@Transactional
	public List<TaxonOccurrence> findByCode(String taxonomyName, String searchString, int maxResults) {
//		Taxonomy taxonomy = taxonomyDao.load(taxonomyName);
//		List<Taxon> list = taxonDao.findByCode(taxonomy.getId(), searchString, maxResults);
		List<TaxonOccurrence> result = new ArrayList<TaxonOccurrence>();
//		for (Taxon taxon : list) {
//			TaxonOccurrence o = new TaxonOccurrence(taxon.getCode(), taxon.getScientificName());
//			result.add(o);
//		}
		return result;
	}

	@Transactional
	public List<TaxonOccurrence> findByScientificName(String taxonomyName, String searchString, int maxResults) {
//		Taxonomy taxonomy = taxonomyDao.load(taxonomyName);
//		List<Taxon> list = taxonDao.findByScientificName(taxonomy.getId(), searchString, maxResults);
		List<TaxonOccurrence> result = new ArrayList<TaxonOccurrence>();
//		for (Taxon taxon : list) {
//			TaxonOccurrence o = new TaxonOccurrence(taxon.getCode(), taxon.getScientificName());
//			result.add(o);
//		}
		return result;
	}
	
	//Do search without CollectRecord and nodeId
	//Added for mobile application by A. Voronov (Arbonaut Ltd.) 
	@Transactional
	public List<TaxonOccurrence> findByVernacularName(String taxonomyName, String searchString, int maxResults) {	
//		Taxonomy taxonomy = taxonomyDao.load(taxonomyName);
		List<TaxonOccurrence> result = new ArrayList<TaxonOccurrence>();		
//		List<TaxonVernacularName> list = taxonVernacularNameDao.findByVernacularName(taxonomy.getId(), searchString, maxResults);
//		for (TaxonVernacularName taxonVernacularName : list) {
//			Integer taxonId = taxonVernacularName.getTaxonSystemId();
//			Taxon taxon = taxonDao.loadById(taxonId);
//			TaxonOccurrence o = new TaxonOccurrence(taxon.getCode(), taxon.getScientificName(), taxonVernacularName.getVernacularName(), taxonVernacularName.getLanguageCode(),
//					taxonVernacularName.getLanguageVariety());
//			result.add(o);
//		}
		return result;
	}
}
