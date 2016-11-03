select caderno_id,
caderno_id as id,
'publico' as secret,
c.Area_Judicial + '-' + c.JudicialAdministrativo + '-' +  CONVERT(char(10), d.data_diario,126) as code, 
c.Area_Judicial + ': Caderno ' + (case when c.JudicialAdministrativo = 'J' then 'Judicial' else 'Administrativo' end) + ' de ' +  CONVERT(char(10), d.data_diario,103) as descr, 
'Caderno' as kind
from caderno c, diario d where c.Diario_ID=d.Diario_ID and
excluido=0 and status='V'
order by d.data_diario desc