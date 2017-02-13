package com.fmyblack.fmyes.Indices;

public enum CreateState {

	Exists{

		@Override
		public String toString() {
			// TODO Auto-generated method stub
			return "already exists";
		}
		
	},
	Success{

		@Override
		public String toString() {
			// TODO Auto-generated method stub
			return "create success";
		}
		
	},
	Failed{

		@Override
		public String toString() {
			// TODO Auto-generated method stub
			return "create failed";
		}
		
	};
	
	@Override
	public abstract String toString();
}
