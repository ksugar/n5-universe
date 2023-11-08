package org.janelia.saalfeldlab.n5.universe.metadata.ome.ngff.v04;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.janelia.saalfeldlab.n5.DatasetAttributes;
import org.janelia.saalfeldlab.n5.universe.metadata.MetadataUtils;
import org.janelia.saalfeldlab.n5.universe.metadata.axes.Axis;

public class OmeNgffMultiScaleMetadataMutable extends OmeNgffMultiScaleMetadata {

	private List<OmeNgffDataset> datasets;

	private List<DatasetAttributes> attributes;

	private List<NgffSingleScaleAxesMetadata> children;

	private Axis[] axes;

	private String path;

	public OmeNgffMultiScaleMetadataMutable() {

		this("");
	}

	public OmeNgffMultiScaleMetadataMutable( final String path) {

		super(-1, MetadataUtils.normalizeGroupPath(path), null, null, null, null, new OmeNgffDataset[]{}, new DatasetAttributes[]{}, null, null, false);

//		final int nd, final String path, final String name,
//		final String type, final String version, final Axis[] axes,
//		final OmeNgffDataset[] datasets, final DatasetAttributes[] childrenAttributes,
//		final CoordinateTransformation<?>[] coordinateTransformations,
//		final OmeNgffDownsamplingMetadata metadata

		setPath( super.basePath );
		datasets = new ArrayList<>();
		attributes = new ArrayList<>();
		children = new ArrayList<>();
	}

//	public static void main(String[] args) {
//
//		final ArrayList<String> list = new ArrayList<String>();
//		// final LinkedList<String> list = new LinkedList<String>();
//
//		list.add("a");
//		list.add("b");
//		// list.set(0, "z");
//		list.add(0, "z");
//		list.add(1, "q");
//
//		list.forEach(System.out::println);
//
//		// System.out.println( list.get(0) );
//		// System.out.println( list.get(1) );
//	}

//	public void addScale(final String path, final CoordinateTransformation<?>[] transforms,
//			final DatasetAttributes attribute) {
//
//		addScaleLevel(-1, path, transforms, attribute);
//	}
//
//	public void addScaleLevel(final int idx, final String path, final CoordinateTransformation<?>[] transforms,
//			final DatasetAttributes attribute) {
//
//		final OmeNgffDataset dset = new OmeNgffDataset();
//		dset.path = path;
//		dset.coordinateTransformations = transforms;
//		addScaleLevel(idx, dset, attribute);
//	}
//
//	public void addScaleLevel(final OmeNgffDataset dataset, final DatasetAttributes attribute) {
//
//		addScaleLevel(-1, dataset, attribute);
//	}
//
//	public void addScaleLevel(int idx, final OmeNgffDataset dataset, final DatasetAttributes attribute) {
//
//		if (idx < 0) {
//			datasets.add(dataset);
//			attributes.add(attribute);
//		} else {
//			datasets.add(idx, dataset);
//			attributes.add(idx, attribute);
//		}
//	}

	@Override
	public String getPath() {

		return path;
	}

	public void setPath(final String path) {

		this.path = path;
	}

	public void addChild(final NgffSingleScaleAxesMetadata child) {

		addChild(-1, child);
	}

	public void addChild(final int idx, final NgffSingleScaleAxesMetadata child) {

		final OmeNgffDataset dset = new OmeNgffDataset();
		// paths are relative to this object
		dset.path = Paths.get(getPath()).relativize(Paths.get(child.getPath())).toString();

		dset.coordinateTransformations = child.getCoordinateTransformations();
		if (idx < 0)
		{
			children.add(child);
			attributes.add(child.getAttributes());
			datasets.add( dset );
		}
		else
		{
			children.add(idx, child);
			attributes.add(idx,child.getAttributes());
			datasets.add(idx, dset );
		}
	}

	public void clear() {

		datasets.clear();
		attributes.clear();
		children.clear();
	}

	@Override
	public NgffSingleScaleAxesMetadata[] getChildrenMetadata()
	{
		if( children == null )
			return null;

		return children.toArray(new NgffSingleScaleAxesMetadata[children.size()]);
	}

	@Override
	public OmeNgffDataset[] getDatasets()
	{
		return datasets.toArray(new OmeNgffDataset[datasets.size()]);
	}

	@Override
	public Axis[] getAxes()
	{
		if( children.size() > 0 )
			return children.get(0).getAxes();

		return null;
	}

}
