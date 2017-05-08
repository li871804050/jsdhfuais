package esclient;


	/*** Eclipse Class Decompiler plugin, copyright (c) 2016 Chen Chao (cnfree2000@hotmail.com) ***/
	

	import java.io.IOException;
	import org.elasticsearch.common.io.stream.StreamInput;
	import org.elasticsearch.common.io.stream.StreamOutput;
	import org.elasticsearch.common.xcontent.ToXContent;
	import org.elasticsearch.common.xcontent.XContentBuilder;
	import org.elasticsearch.common.xcontent.ToXContent.Params;

	public class terms implements ToXContent {
		final String field;
		final String term;
		
		

		terms(String field, String term) {
			this.field = field;
			this.term = term;
			
		}

		static terms readFrom(StreamInput in) throws IOException {
			return new terms(in.readString(), in.readString());
		}

		void writeTo(StreamOutput out) throws IOException {
			out.writeString(this.field);
			out.writeString(this.term);
			
			
		}

		public XContentBuilder toXContent(XContentBuilder builder, Params params) throws IOException {
			boolean returnDetailedInfo = params.paramAsBoolean("returnDetailedInfo", false);
			builder.field("field", this.field);
			builder.field("term", this.term);
		
			if (returnDetailedInfo) {
				builder.field("fg");
				builder.field("bg");
			}

			return builder;
		}

		public terms.termsId getId() {
			return createId(this.field, this.term);
		}

		public static terms.termsId createId(String field, String term) {
			return new terms.termsId(field, term);
		}

		public String toString() {
			return this.getId().toString();
		}

		public String getField() {
			return this.field;
		}

		public String getTerm() {
			return this.term;
		}

		

		

		

		public static class termsId {
			private final String field;
			private final String term;

			public termsId(String field, String term) {
				this.field = field;
				this.term = term;
			}

			public boolean equals(Object o) {
				if (this == o) {
					return true;
				} else if (o != null && this.getClass() == o.getClass()) {
					terms.termsId vertexId = (terms.termsId) o;
					if (this.field != null) {
						if (!this.field.equals(vertexId.field)) {
							return false;
						}
					} else if (vertexId.field != null) {
						return false;
					}

					if (this.term != null) {
						if (this.term.equals(vertexId.term)) {
							return true;
						}
					} else if (vertexId.term == null) {
						return true;
					}

					return false;
				} else {
					return false;
				}
			}

			public int hashCode() {
				int result = this.field != null ? this.field.hashCode() : 0;
				result = 31 * result + (this.term != null ? this.term.hashCode() : 0);
				return result;
			}

			public String toString() {
				return this.field + ":" + this.term;
			}
		}
	}

