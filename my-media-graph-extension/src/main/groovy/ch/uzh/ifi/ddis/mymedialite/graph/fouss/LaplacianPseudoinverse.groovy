package ch.uzh.ifi.ddis.mymedialite.graph.fouss

import groovy.transform.CompileStatic
import no.uib.cipr.matrix.DenseMatrix
import no.uib.cipr.matrix.Matrix
import no.uib.cipr.matrix.MatrixEntry

import org.ejml.simple.SimpleMatrix

import Jama.SingularValueDecomposition
import ch.uzh.ifi.ddis.mymedialite.graph.cooper.matrix.AbstractP

import com.tinkerpop.blueprints.Vertex

class LaplacianPseudoinverse extends AbstractP {

	public LaplacianPseudoinverse() {
		super(-1.0, -1.0)
	}

	@Override
	public void train() {
		super.train()
		vertexList = getSortedListOfVertices(graph)
		createLaplacianPseudoinverse(getSortedListOfVertices(graph))
	}

	public void createLaplacianPseudoinverse(def vertexList) {

		Matrix adjacency = createAdjacencyMatrix(vertexList, userItemFeedbackEdgeLabel)
		println("----- Adjacency -----")
		//println(adjacency)
		Matrix degree = createDegreeMatrix(vertexList, userItemFeedbackEdgeLabel)
		println("----- Degree -----")
		//println(degree)
		Matrix laplacian = degree.add(-1.0, adjacency)
		println("----- Laplacian -----")
		//println(laplacian)
		queryMatrix = getPseudoinverseWithEJML(laplacian)
		println("----- Laplacian Pseudoinverse -----")
		//println(queryMatrix)
	}

	@CompileStatic
	Matrix getPseudoinverseWithEJML(DenseMatrix matrix){
		Pseudoinverter.updateMacheps()
		SimpleMatrix inp  = convertToEJMLMatrix(matrix)
		println("----- pseudoinvert Inp -----")
		//println(inp)
		SimpleMatrix out = inp.pseudoInverse()
		println("----- pseudoinvert Out -----")
		//println(out)
		return convertToFommilMatrix(out)
	}

	@CompileStatic
	Matrix getPseudoinverseWithJamal(DenseMatrix matrix){
		Pseudoinverter.updateMacheps()
		Jama.Matrix inp  = convertToJamaMatrix(matrix)
		println("----- Pseudoinverter Inp -----")
		println(inp.getArray())
		Jama.Matrix out = Pseudoinverter.pinv(inp)
		println("----- Pseudoinverter Out -----")
		println(out.getArray())
		return convertToFommilMatrix(out)
	}

	Jama.Matrix convertToJamaMatrix(Matrix m){
		double[][] a = new double[m.numRows()][m.numColumns()]
		for (MatrixEntry e : m){
			a[e.row()][e.column()] = e.get()
		}
		return new Jama.Matrix(a)
	}

	SimpleMatrix convertToEJMLMatrix(Matrix m){
		double[][] a = new double[m.numRows()][m.numColumns()]
		for (MatrixEntry e : m){
			a[e.row()][e.column()] = e.get()
		}
		return new SimpleMatrix(a)
	}

	@CompileStatic
	Matrix convertToFommilMatrix(Jama.Matrix matrix){
		return new DenseMatrix(matrix.getArray())
	}

	@CompileStatic
	Matrix convertToFommilMatrix(SimpleMatrix matrix){
		double[][] a = new double[matrix.numRows()][matrix.numCols()]
		for(int r = 0; r < matrix.numRows(); r++ ){
			for(int c = 0; c < matrix.numCols(); c++ ){
				a[r][c] = matrix.get(r, c)
			}
		}
		return new DenseMatrix(a)
	}


	// Adapted for no.uib.cipr.matrix from http://the-lost-beauty.blogspot.ch/2009/04/moore-penrose-pseudoinverse-in-jama.html
	public static class Pseudoinverter {
		/**
		 * The difference between 1 and the smallest exactly representable number
		 * greater than one. Gives an upper bound on the relative error due to
		 * rounding of floating point numbers.
		 */
		public static double MACHEPS = 2E-16

		/**
		 * Updates MACHEPS for the executing machine.
		 */
		public static void updateMacheps() {
			MACHEPS = 1
			while(true){
				MACHEPS /= 2
				if (1 + MACHEPS / 2 != 1) break
			}
		}

		/**
		 * Computes the Moore–Penrose pseudoinverse using the SVD method.
		 *
		 * Modified version of the original implementation by Kim van der Linde.
		 */
		@CompileStatic
		public static Jama.Matrix pinv(Jama.Matrix x) {
			//			if (x.rank() < 1)
			//				return null
			if (x.getColumnDimension() > x.getRowDimension())
				return pinv(x.transpose()).transpose()
			println("Start SingularValueDecomposition ...")
			SingularValueDecomposition svdX = new SingularValueDecomposition(x)
			println("SingularValueDecomposition done.")
			double[] singularValues = svdX.getSingularValues()
			double tol = Math.max(x.getColumnDimension(), x.getRowDimension()) * singularValues[0] * MACHEPS
			double[] singularValueReciprocals = new double[singularValues.length]
			for (int i = 0; i < singularValues.length; i++)
				singularValueReciprocals[i] = Math.abs(singularValues[i]) < tol ? 0 : (1.0 / singularValues[i])
			double[][] u = svdX.getU().getArray()
			double[][] v = svdX.getV().getArray()
			int min = Math.min(x.getColumnDimension(), u[0].length)
			double[][] inverse = new double[x.getColumnDimension()][x.getRowDimension()]
			for (int i = 0; i < x.getColumnDimension(); i++)
				for (int j = 0; j < u.length; j++)
					for (int k = 0; k < min; k++)
						inverse[i][j] += v[i][k] * singularValueReciprocals[k] * u[j][k]

			println("----- Pseudoinverter Mid -----")
			println(inverse)
			return new Jama.Matrix(inverse)
		}
	}
}
