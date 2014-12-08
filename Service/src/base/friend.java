 package base;

//???????б??е????????
public class friend { 
    
    private String resId; //???????????id 
    private String name; //????????
    private String phone; //???????????????
    
    private String illness; //????
    private String realname; //????????
    private String age; //????
    private String sex; //???
    private String credit; //????
    private String score; //???
    private String address; //???
     
    //??????
    public friend(String resId, String name, String phone) { 
        this.resId  = resId; 
        this.name   = name; 
        this.phone = phone; 
    } 
    
    //??????
    public friend(String resId, String name, String phone, String illness, String realname, String age, String sex, 
    		String credit, String score, String address)
    {
    	this.resId = resId;
    	this.name = name;
    	this.phone = phone;
    	
    	this.illness = illness;
    	this.realname = realname;
    	this.age = age;
    	this.sex = sex;
    	this.credit = credit;
    	this.score = score;
    	this.address = address;
    }
     
    public void setImageId(String resId) { 
        this.resId  = resId; 
    } 
     
    public String getImageId() { 
        return resId; 
    } 
     
    public void setName(String name) { 
        this.name   = name; 
    } 
     
    public String getName() { 
        return name; 
    } 
     
    public void setPhone(String phone) { 
        this.phone = phone; 
    } 
     
    public String getPhone() { 
        return phone; 
    } 
    
    public void setIllness(String illness)
    {
    	this.illness = illness;
    }
    
    public String getIllness()
    {
    	return illness;
    }
    
    public void setRealname(String realname)
    {
    	this.realname = realname;
    }
    
    public String getRealname()
    {
    	return realname;
    }
     
    public void setAge(String age)
    {
    	this.age = age;
    }
    
    public String getage()
    {
    	return age;
    }
    
    public void setSex(String sex)
    {
    	this.sex = sex;
    }
    
    public String getSex()
    {
    	return sex;
    }
    
    public void setCredit(String credit)
    {
    	this.credit = credit;
    }
    
    public String getCredit()
    {
    	return credit;
    }
    
    public void setScore(String score)
    {
    	this.score = score;
    }
    
    public String getScore()
    {
    	return score;
    }
    
    public void setAddress(String address)
    {
    	this.address = address;
    }
    
    public String getAddress()
    {
    	return address;
    }

 
} 
