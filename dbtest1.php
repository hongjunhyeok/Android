<HTML>
    <HEAD>
        <TITLE> PHP 테스트 </TITLE>
        
    </HEAD>
    <BODY>
        <?php
        $pdo=new PDO('mysql:host=localhost;dbname=sampledb;charset=utf8','sample','password');//인수에 호스트명, 사용자명, 패스워드, 데이터베이스명 입력
        print 'PDO 클래스로 접속하였습니다.';
        print '접속성공';
        $pdo=null; //접속해제
        
        ?>
        
        
    </BODY>
    
    
    
</HTML>



/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

