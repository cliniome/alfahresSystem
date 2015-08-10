package test;

/**
 * Created by snouto on 09/08/15.
 */

import com.degla.restful.models.*;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by snouto on 09/08/15.
 */
public class TestSessions {


    public static void main(String... args)
    {
        Runnable[] runnables = new Runnable[3];

        runnables[0]= new RequestOperation("ibrahim","ibrahim");
        runnables[1]= new RequestOperation("serene","serene");
        runnables[2]= new RequestOperation("mohamed","mohamed");


        //run all of them simulatenously

        for(Runnable runnable : runnables)
        {
            Thread thread = new Thread(runnable);
            thread.start();
        }


    }



    static class RequestOperation implements Runnable
    {

        private String username;
        private String password;

        public RequestOperation(String username , String password)
        {
            this.username = username;
            this.password = password;
        }



        @Override
        public void run() {
            try
            {
                System.out.println(String.format("Context : %s(%s) begins The Sending process",
                        this.username, this.password));

                //Set the System Settings Manager
                AlfahresConnection connection = new AlfahresConnection();


                RestfulEmployee emp = new RestfulEmployee();
                emp.setUserName(this.username);
                emp.setPassword(this.password);
                HttpResponse response = connection.setAuthorization(emp)
                        .setMethodType(AlfahresConnection.GET_HTTP_METHOD)
                        .path("files/new")
                        .call(new TypeToken<List<RestfulRequest>>() {
                        }.getType());

                if(response != null && HttpResponse.OK_HTTP_CODE == Integer.parseInt(
                        response.getResponseCode()
                ))
                {
                    //get the list of files
                    List<RestfulRequest> files = (List<RestfulRequest>) response.getPayload();

                    if(files != null)
                    {
                        for(RestfulRequest file : files)
                        {
                            this.markFileAsMissing(file);
                        }

                        //Done sending all files
                        System.out.println("Done Sending All The Files");
                    }
                }


            }catch (Exception s)
            {
                s.printStackTrace();
            }
        }

        private void markFileAsMissing(RestfulRequest file) {



            //Create a sync Batch

            SyncBatch batch = new SyncBatch();
            batch.setFiles(new ArrayList<RestfulFile>());
            RestfulEmployee emp = new RestfulEmployee();
            emp.setUserName(this.username);
            emp.setPassword(this.password);

            file.setState(FileModelStates.MISSING.toString());
            RestfulFile rfile = new RestfulFile();
            rfile.setAppointmentDate(file.getAppointmentDate());
            rfile.setAppointmentDateH(file.getAppointmentDateH());
            rfile.setAppointmentMadeBy(file.getAppointmentMadeBy());
            rfile.setAppointmentTime(file.getAppointmentTime());
            rfile.setAppointmentType(file.getAppointmentType());
            rfile.setBatchRequestNumber(file.getBatchRequestNumber());
            rfile.setClinicCode(file.getClinicCode());
            rfile.setClinicDocCode(file.getClinicDocCode());
            rfile.setClinicDocName(file.getClinicDocName());
            rfile.setClinicName(file.getClinicName());
            rfile.setDescription("");
            rfile.setCabinetId(file.getFileNumber());
            rfile.setEmp(emp);
            rfile.setFileNumber(file.getFileNumber());
            rfile.setInpatient(file.isInpatient());
            rfile.setMultipleClinics(file.isInpatient());
            rfile.setOperationDate(new Date().getTime());
            rfile.setPatientName(file.getPatientName());
            rfile.setPatientNumber(file.getPatientNumber());
            rfile.setState(FileModelStates.MISSING.toString());
            rfile.setTemporaryCabinetId("");
            batch.getFiles().add(rfile);

            AlfahresConnection conn = new AlfahresConnection();
            //now call the sync now
            AlfahresConnection connection = conn;
            HttpResponse response = connection.path("sync/now").setAuthorization(emp.getUserName(),emp.getPassword())
                    .setMethodType(AlfahresConnection.POST_HTTP_METHOD)
                    .setBody(batch)
                    .call(SyncBatch.class);

            if(response != null && HttpResponse.OK_HTTP_CODE ==
                    Integer.parseInt(response.getResponseCode()))
            {
                System.out.println(String.format("Done sending File : %s",file.getFileNumber()));

            }







        }
    }
}
