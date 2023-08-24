package org.janelia.saalfeldlab.n5.universe.metadata;

import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashMap;

import org.janelia.saalfeldlab.n5.DatasetAttributes;
import org.janelia.saalfeldlab.n5.N5URI;
import org.janelia.saalfeldlab.n5.universe.N5TreeNode;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;

import net.imglib2.realtransform.AffineGet;
import net.imglib2.realtransform.InvertibleRealTransformSequence;
import net.imglib2.realtransform.Scale;
import net.imglib2.realtransform.Scale2D;
import net.imglib2.realtransform.Scale3D;
import net.imglib2.realtransform.ScaleAndTranslation;
import net.imglib2.realtransform.Translation;
import net.imglib2.realtransform.Translation2D;
import net.imglib2.realtransform.Translation3D;

public class MetadataUtils
{

	/**
	 * Returns a new {@link N5SingleScaleMetadata} equal to the baseMetadata, but with
	 * {@link DatasetAttributes} coming from datasetMetadata.ew
	 * <p>
	 *
	 * @param baseMetadata metadata
	 * @param datasetMetadata dataset metadata
	 * @return the single scale metadata
	 */
	public static N5SingleScaleMetadata setDatasetAttributes( final N5SingleScaleMetadata baseMetadata, final N5DatasetMetadata datasetMetadata )
	{
		if( baseMetadata.getPath().equals( datasetMetadata.getPath() ))
			return new N5SingleScaleMetadata( baseMetadata.getPath(), baseMetadata.spatialTransform3d(),
					baseMetadata.getDownsamplingFactors(), baseMetadata.getPixelResolution(), baseMetadata.getOffset(),
					baseMetadata.unit(), datasetMetadata.getAttributes() );
		else
			return null;
	}

	public static N5SingleScaleMetadata[] updateChildrenDatasetAttributes( final N5SingleScaleMetadata[] baseMetadata, final N5DatasetMetadata[] datasetMetadata )
	{
		final HashMap<String,N5SingleScaleMetadata> bases = new HashMap<>();
		Arrays.stream( baseMetadata ).forEach( x -> { bases.put( x.getPath(), x ); } );

		return ( N5SingleScaleMetadata[] ) Arrays.stream( datasetMetadata ).map( x -> {
			final N5SingleScaleMetadata b = bases.get( x.getPath() );
			if( b == null )
				return null;
			else
				return setDatasetAttributes( b, x );
		} ).filter( x -> x != null ).toArray();
	}

	public static void updateChildrenMetadata( final N5TreeNode parent, final N5Metadata[] childrenMetadata )
	{
		final HashMap<String,N5Metadata> children = new HashMap<>();
		Arrays.stream( childrenMetadata ).forEach( x -> { children.put( x.getPath(), x ); } );
		parent.childrenList().forEach( c -> {
			final N5Metadata m = children.get( c.getPath() );
			if( m != null )
				c.setMetadata( m );
		});
	}

	public static String canonicalPath( final N5TreeNode parent, final String child )
	{
		return canonicalPath( parent.getPath(), child );
	}

	public static String canonicalPath( final String parent, final String child )
	{
		try
		{
			final N5URI url = new N5URI( "?/" + parent + "/" + child );
			return url.normalizeGroupPath();
		}
		catch ( final URISyntaxException e )
		{
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Element-wise power. Returns an array y such that y[i] = x[i] ^ d
	 *
	 * @param x array
	 * @param d exponent
	 * @return result
	 */
	public static double[] pow( final double[] x, final int d )
	{
		final double[] y = new double[ x.length ];
		Arrays.fill( y, 1 );
		for ( int i = 0; i < d; i++ )
			for ( int j = 0; j < x.length; j++ )
				y[ j ] *= x[ j ];

		return y;
	}

	/**
	 * Gets a String from a {@link JsonElement} if possible, returning null if the
	 * element is {@link JsonNull}.
	 *
	 * @param element the json element
	 * @return a string
	 */
	public static String getStringNullable(final JsonElement element) {
		if (element.isJsonNull())
			return null;
		else
			return element.getAsString();
	}

	/**
	 * Returns the most efficient transform given the input scale and translation parameters.
	 * If both are null, this method will return null;
	 *
	 * @param scale the scale parameters
	 * @param translation the translation parameters
	 * @return an appropriate AffineGet
	 */
	public static AffineGet scaleTranslationTransforms(final double[] scale, final double[] translation) {

		if (translation != null) {

			if( scale != null ) {
				return new ScaleAndTranslation(scale, translation);
			}
			else {
				// scale null, translation not null
				if (translation.length == 2)
					return new Translation2D(translation);
				else if (translation.length == 3)
					return new Translation3D(translation);
				else
					return new Translation(translation);
			}

		} else if (scale != null) {
			// scale not null, translation null
			if (scale.length == 2)
				return new Scale2D(scale);
			else if (scale.length == 3)
				return new Scale3D(scale);
			else
				return new Scale(scale);

		}
		return null;
	}

}
