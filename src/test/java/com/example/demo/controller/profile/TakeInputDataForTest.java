package com.example.demo.controller.profile;

import com.example.demo.domain.Fabrics;
import com.example.demo.domain.UserFabrics;
import com.example.demo.domain.Users;

import java.util.ArrayList;
import java.util.List;

public class TakeInputDataForTest {

    public static List<Fabrics> fabricList(){

        List<Fabrics> fabricsListFoTest = new ArrayList<>();

        Fabrics f1 = new Fabrics(1.0,"firstFabric",3.0,0.00001,"image-1");
        Fabrics f2 = new Fabrics(5.0,"secondFabric",15.0,0.00006,"image-2");
        Fabrics f3 = new Fabrics(10.0,"threadFabric",30.0,0.00015,"image-3");
        Fabrics f4 = new Fabrics(50.0,"forthFabric",150.0,0.0008,"image-4");

        fabricsListFoTest.add(f1);
        fabricsListFoTest.add(f2);
        fabricsListFoTest.add(f3);
        fabricsListFoTest.add(f4);

        return fabricsListFoTest;
    }

    public static List<Users> usersList(){

        List<Users> usersListFoTest = new ArrayList<>();

        Users u1 = new Users("firstUser","firstUser@some.net","USER","somePassword-1","someToken-1");
        Users u2 = new Users("secondUser","secondUser@some.net","USER","somePassword-2","someToken-2");
        Users u3 = new Users("threadUser","threadUser@some.net","USER","somePassword-3","someToken-3");
        Users u4 = new Users("forthUser","forthUser@some.net","USER","somePassword-4","someToken-4");

        usersListFoTest.add(u1);
        usersListFoTest.add(u2);
        usersListFoTest.add(u3);
        usersListFoTest.add(u4);

        return usersListFoTest;
    }

    public static List<UserFabrics> userFabricList(){

        List<UserFabrics> usersFabricListFoTest = new ArrayList<>();

        Fabrics f1 = new Fabrics(1.0,"firstFabric",3.0,0.00001,"image-1");
        Fabrics f2 = new Fabrics(5.0,"secondFabric",15.0,0.00006,"image-2");

        Users u1 = new Users("SomeUser","User@some.net","USER","somePassword","user-token");

        usersFabricListFoTest.add(new UserFabrics(u1,f1,1,0.00001));
        usersFabricListFoTest.add(new UserFabrics(u1,f2,2,0.00012));

        return usersFabricListFoTest;
    }

    public static Fabrics getOneFabric(){
        return new Fabrics(1.0, "OneOfFabric", 3.0, 0.00001);
    }

    public static UserFabrics getUserFabric(Users user){
        return new UserFabrics(user,new Fabrics(1.0,"firstFabric",3.0,0.00001,"image-1"),
                1,0.00001);
    }
}
