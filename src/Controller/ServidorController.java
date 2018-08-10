package Controller;

import java.io.IOException;

import Service.ServidorService;

public class ServidorController {

	public static void main(String[] args) {
		try {
			ServidorService ss = new ServidorService(9000);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
}
