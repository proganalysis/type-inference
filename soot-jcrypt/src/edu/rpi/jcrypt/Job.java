package edu.rpi.jcrypt;

import java.util.ArrayList;
import java.util.List;

import edu.rpi.AnnotatedValue;

public class Job {
	
	private List<AnnotatedValue> mik, miv, mok, mov, rik, riv, rok, rov, cik, civ,
		cok, cov, pik, piv;
	
	public Job() {
		mik = new ArrayList<>();
		miv = new ArrayList<>();
		mok = new ArrayList<>();
		mov = new ArrayList<>();
		rik = new ArrayList<>();
		riv = new ArrayList<>();
		rok = new ArrayList<>();
		rov = new ArrayList<>();
		cik = new ArrayList<>();
		civ = new ArrayList<>();
		cok = new ArrayList<>();
		cov = new ArrayList<>();
		pik = new ArrayList<>();
		piv = new ArrayList<>();
	}

	public List<AnnotatedValue> getMik() {
		return mik;
	}

	public List<AnnotatedValue> getMiv() {
		return miv;
	}

	public List<AnnotatedValue> getMok() {
		return mok;
	}

	public List<AnnotatedValue> getMov() {
		return mov;
	}

	public List<AnnotatedValue> getRik() {
		return rik;
	}

	public List<AnnotatedValue> getRiv() {
		return riv;
	}

	public List<AnnotatedValue> getRok() {
		return rok;
	}

	public List<AnnotatedValue> getRov() {
		return rov;
	}

	public List<AnnotatedValue> getCik() {
		return cik;
	}

	public List<AnnotatedValue> getCiv() {
		return civ;
	}

	public List<AnnotatedValue> getCok() {
		return cok;
	}

	public List<AnnotatedValue> getCov() {
		return cov;
	}

	public List<AnnotatedValue> getPik() {
		return pik;
	}

	public List<AnnotatedValue> getPiv() {
		return piv;
	}
	
}
