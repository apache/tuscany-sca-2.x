package org.apache.tuscany.sca.binding.corba.testing.servants;

import org.apache.tuscany.sca.binding.corba.testing.generated.SimpleStruct;
import org.apache.tuscany.sca.binding.corba.testing.generated.SimpleStructHolder;
import org.apache.tuscany.sca.binding.corba.testing.generated.SomeStruct;
import org.apache.tuscany.sca.binding.corba.testing.generated.SomeStructHolder;
import org.apache.tuscany.sca.binding.corba.testing.generated._TestObjectImplBase;
import org.apache.tuscany.sca.binding.corba.testing.generated.long_seq1Holder;
import org.apache.tuscany.sca.binding.corba.testing.generated.long_seq2Holder;
import org.apache.tuscany.sca.binding.corba.testing.generated.long_seq3Holder;

public class TestObjectServant extends _TestObjectImplBase {

	private static final long serialVersionUID = 1L;

	public int[] setLongSeq1(long_seq1Holder arg) {
		return arg.value;
	}

	public int[][] setLongSeq2(long_seq2Holder arg) {
		
		return arg.value;
	}

	public int[][][] setLongSeq3(long_seq3Holder arg) {
		return arg.value;
	}

	public SimpleStruct setSimpleStruct(SimpleStructHolder arg) {
		return arg.value;
	}

	public SomeStruct setStruct(SomeStructHolder arg) {
		return arg.value;
	}

	public SomeStruct pickStructFromArgs(SomeStructHolder arg1,
			SomeStructHolder arg2, SomeStructHolder arg3, int structNumber) {
		switch (structNumber) {
		case 1:
			return arg1.value;
		case 2:
			return arg2.value;
		case 3:
			return arg3.value;
		default:
			return arg1.value;
		}
	}

}
